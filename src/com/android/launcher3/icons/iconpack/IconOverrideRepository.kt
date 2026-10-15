/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.icons.iconpack

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.media.ExifInterface
import com.android.launcher3.util.ComponentKey
import com.android.launcher3.graphics.ThemeManager
import java.io.File
import java.io.FileOutputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object IconOverrideRepository {
    private const val PREFS_NAME = "icon_overrides"
    private const val KEY_PREFIX = "override:"
    private const val LABEL_PREFIX = "label:"
    private const val IMAGE_PREFIX = "image:"
    private const val IMAGE_DIRECTORY = "custom_icons"
    private const val MAX_DECODE_IMAGE_SIZE = 2048
    private const val MAX_IMAGE_SIZE = 512
    private val maps = ConcurrentHashMap<String, MutableMap<ComponentKey, IconPackEntry>>()
    private val labels = ConcurrentHashMap<String, MutableMap<ComponentKey, String>>()

    fun get(context: Context, key: ComponentKey): IconPackEntry? =
        getMap(context)[key]

    fun set(context: Context, key: ComponentKey, entry: IconPackEntry) {
        getMap(context)[key] = entry
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val oldImage = getCustomImageFile(context, key)
        preferences
            .edit()
            .putString(KEY_PREFIX + key, "${entry.packPackageName}|${entry.drawableName}")
            .remove(IMAGE_PREFIX + key)
            .apply()
        oldImage?.delete()
    }

    fun remove(context: Context, key: ComponentKey) {
        getMap(context).remove(key)
        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val oldImage = getCustomImageFile(context, key)
        preferences
            .edit()
            .remove(KEY_PREFIX + key)
            .remove(IMAGE_PREFIX + key)
            .apply()
        oldImage?.delete()
    }

    fun hasOverride(context: Context, key: ComponentKey): Boolean =
        get(context, key) != null || getCustomImageFile(context, key) != null

    fun getCustomImageDrawable(context: Context, key: ComponentKey): Drawable? {
        val file = getCustomImageFile(context, key) ?: return null
        return runCatching {
            val source = BitmapFactory.decodeFile(file.absolutePath)
                ?: return null
            val shaped = createShapedIcon(context, source)
            if (shaped !== source) source.recycle()
            AdaptiveIconDrawable(
                ColorDrawable(Color.TRANSPARENT),
                BitmapDrawable(context.resources, shaped),
            )
        }.getOrNull()
    }

    private fun createShapedIcon(context: Context, source: Bitmap): Bitmap {
        val bounds = Rect(0, 0, source.width, source.height)
        val path = ThemeManager.INSTANCE.get(context).iconShape.getPath(bounds)
        val output = Bitmap.createBitmap(source.width, source.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val iconPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG).apply {
            shader = BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }
        canvas.drawPath(path, iconPaint)
        iconPaint.shader = null
        return output
    }

    fun setCustomImage(context: Context, key: ComponentKey, uri: Uri): Boolean {
        val bitmap = decodeCustomImage(context, uri) ?: return false
        return setCustomImage(context, key, bitmap).also { bitmap.recycle() }
    }

    fun setCustomImage(context: Context, key: ComponentKey, uri: Uri, cropBounds: RectF): Boolean {
        val bitmap = decodeOriginalCrop(context, uri, cropBounds)
            ?: decodePreviewCrop(context, uri, cropBounds)
            ?: return false
        return setCustomImage(context, key, bitmap).also { bitmap.recycle() }
    }

    fun decodeCustomImage(context: Context, uri: Uri): Bitmap? = try {
        ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri)) {
                decoder, info, _ ->
            val width = info.size.width
            val height = info.size.height
            val largestDimension = maxOf(width, height)
            if (largestDimension > MAX_DECODE_IMAGE_SIZE) {
                val scale = MAX_DECODE_IMAGE_SIZE.toFloat() / largestDimension
                decoder.setTargetSize(
                    (width * scale).toInt().coerceAtLeast(1),
                    (height * scale).toInt().coerceAtLeast(1),
                )
            }
            decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
        }
    } catch (_: Exception) {
        null
    }

    private fun decodeOriginalCrop(context: Context, uri: Uri, bounds: RectF): Bitmap? {
        return try {
            val orientation = context.contentResolver.openInputStream(uri)?.use {
                ExifInterface(it).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL,
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
            val sourceBounds = mapToEncodedBounds(bounds, orientation)
            val decoder = context.contentResolver.openInputStream(uri)?.use {
                BitmapRegionDecoder.newInstance(it, false)
            } ?: return null

            val decoded = try {
                val crop = createCropRect(decoder.width, decoder.height, sourceBounds)
                val options = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                    inSampleSize = sampleSizeFor(crop.width(), MAX_IMAGE_SIZE)
                }
                decoder.decodeRegion(crop, options)
            } finally {
                decoder.recycle()
            } ?: return null

            val oriented = orientBitmap(decoded, orientation)
            if (oriented !== decoded) decoded.recycle()
            scaleToIconSize(oriented)
        } catch (_: Exception) {
            null
        }
    }

    private fun decodePreviewCrop(context: Context, uri: Uri, bounds: RectF): Bitmap? {
        val preview = decodeCustomImage(context, uri) ?: return null
        return try {
            val crop = createCropRect(preview.width, preview.height, bounds)
            scaleToIconSize(Bitmap.createBitmap(preview, crop.left, crop.top, crop.width(), crop.height()))
        } catch (_: Exception) {
            null
        } finally {
            preview.recycle()
        }
    }

    private fun createCropRect(width: Int, height: Int, bounds: RectF): Rect {
        val side = minOf(
            (bounds.width() * width).toInt(),
            (bounds.height() * height).toInt(),
        ).coerceIn(1, minOf(width, height))
        val centerX = ((bounds.left + bounds.right) * width / 2f).toInt()
        val centerY = ((bounds.top + bounds.bottom) * height / 2f).toInt()
        val left = (centerX - side / 2).coerceIn(0, width - side)
        val top = (centerY - side / 2).coerceIn(0, height - side)
        return Rect(left, top, left + side, top + side)
    }

    private fun mapToEncodedBounds(bounds: RectF, orientation: Int): RectF = when (orientation) {
        ExifInterface.ORIENTATION_FLIP_HORIZONTAL ->
            RectF(1f - bounds.right, bounds.top, 1f - bounds.left, bounds.bottom)
        ExifInterface.ORIENTATION_ROTATE_180 ->
            RectF(1f - bounds.right, 1f - bounds.bottom, 1f - bounds.left, 1f - bounds.top)
        ExifInterface.ORIENTATION_FLIP_VERTICAL ->
            RectF(bounds.left, 1f - bounds.bottom, bounds.right, 1f - bounds.top)
        ExifInterface.ORIENTATION_TRANSPOSE ->
            RectF(bounds.top, bounds.left, bounds.bottom, bounds.right)
        ExifInterface.ORIENTATION_ROTATE_90 ->
            RectF(bounds.top, 1f - bounds.right, bounds.bottom, 1f - bounds.left)
        ExifInterface.ORIENTATION_TRANSVERSE ->
            RectF(1f - bounds.bottom, 1f - bounds.right, 1f - bounds.top, 1f - bounds.left)
        ExifInterface.ORIENTATION_ROTATE_270 ->
            RectF(1f - bounds.bottom, bounds.left, 1f - bounds.top, bounds.right)
        else -> RectF(bounds)
    }

    private fun orientBitmap(bitmap: Bitmap, orientation: Int): Bitmap {
        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.setScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.setRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.setRotate(-90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
            else -> return bitmap
        }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun sampleSizeFor(sourceSize: Int, targetSize: Int): Int {
        var sampleSize = 1
        while (sourceSize / (sampleSize * 2) >= targetSize) sampleSize *= 2
        return sampleSize
    }

    private fun scaleToIconSize(bitmap: Bitmap): Bitmap {
        val largestDimension = maxOf(bitmap.width, bitmap.height)
        if (largestDimension <= MAX_IMAGE_SIZE) return bitmap
        val scale = MAX_IMAGE_SIZE.toFloat() / largestDimension
        return Bitmap.createScaledBitmap(
            bitmap,
            (bitmap.width * scale).toInt().coerceAtLeast(1),
            (bitmap.height * scale).toInt().coerceAtLeast(1),
            true,
        ).also { if (it !== bitmap) bitmap.recycle() }
    }

    fun setCustomImage(context: Context, key: ComponentKey, bitmap: Bitmap): Boolean {
        val outputBitmap = if (maxOf(bitmap.width, bitmap.height) > MAX_IMAGE_SIZE) {
            val scale = MAX_IMAGE_SIZE.toFloat() / maxOf(bitmap.width, bitmap.height)
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt().coerceAtLeast(1),
                (bitmap.height * scale).toInt().coerceAtLeast(1),
                true,
            )
        } else {
            bitmap
        }

        val directory = File(context.filesDir, IMAGE_DIRECTORY).apply { mkdirs() }
        val file = File(directory, "${UUID.randomUUID()}.png")
        val saved = try {
            FileOutputStream(file).use { output ->
                outputBitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
            }
        } catch (_: Exception) {
            false
        } finally {
            if (outputBitmap !== bitmap) outputBitmap.recycle()
        }
        if (!saved) {
            file.delete()
            return false
        }

        val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val oldImage = getCustomImageFile(context, key)
        getMap(context).remove(key)
        preferences.edit()
            .putString(IMAGE_PREFIX + key, file.name)
            .remove(KEY_PREFIX + key)
            .apply()
        oldImage?.delete()
        return true
    }

    fun getLabel(context: Context, key: ComponentKey): String? = getLabels(context)[key]

    fun setLabel(context: Context, key: ComponentKey, label: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        if (label.isBlank()) {
            getLabels(context).remove(key)
            prefs.edit().remove(LABEL_PREFIX + key).apply()
        } else {
            getLabels(context)[key] = label
            prefs.edit().putString(LABEL_PREFIX + key, label).apply()
        }
    }

    fun getPackageState(context: Context, packageName: String, user: android.os.UserHandle): String {
        val overrides = getMap(context)
            .asSequence()
            .filter { it.key.componentName.packageName == packageName && it.key.user == user }
            .sortedBy { it.key.componentName.className }
            .joinToString(";") { (key, value) ->
                "$key=${value.packPackageName}/${value.drawableName}"
            }
        val customLabels = getLabels(context)
            .asSequence()
            .filter { it.key.componentName.packageName == packageName && it.key.user == user }
            .sortedBy { it.key.componentName.className }
            .joinToString(";") { (key, value) -> "$key=$value" }
        val customImages = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).all
            .asSequence()
            .filter { it.key.startsWith(IMAGE_PREFIX) && it.value is String }
            .mapNotNull { (prefKey, value) ->
                val key = ComponentKey.fromString(prefKey.removePrefix(IMAGE_PREFIX))
                    ?: return@mapNotNull null
                if (key.componentName.packageName != packageName || key.user != user) {
                    return@mapNotNull null
                }
                "$key=$value"
            }
            .sorted()
            .joinToString(";")
        return listOf(overrides, customLabels, customImages).joinToString("|")
    }

    private fun getCustomImageFile(context: Context, key: ComponentKey): File? {
        val filename = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(IMAGE_PREFIX + key, null) ?: return null
        if (filename.contains('/') || filename.contains('\\')) return null
        return File(File(context.filesDir, IMAGE_DIRECTORY), filename).takeIf { it.isFile }
    }

    private fun getMap(context: Context): MutableMap<ComponentKey, IconPackEntry> =
        maps.getOrPut(context.packageName) {
            val map = mutableMapOf<ComponentKey, IconPackEntry>()
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .all
                .forEach { (key, value) ->
                    val componentKey = ComponentKey.fromString(key.removePrefix(KEY_PREFIX))
                    val parts = (value as? String)?.split('|', limit = 2)
                    if (key.startsWith(KEY_PREFIX) && componentKey != null && parts?.size == 2) {
                        map[componentKey] = IconPackEntry(parts[0], parts[1])
                    }
                }
            map
        }

    private fun getLabels(context: Context): MutableMap<ComponentKey, String> =
        labels.getOrPut(context.packageName) {
            val map = mutableMapOf<ComponentKey, String>()
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .all
                .forEach { (key, value) ->
                    if (!key.startsWith(LABEL_PREFIX)) return@forEach
                    val componentKey = ComponentKey.fromString(key.removePrefix(LABEL_PREFIX))
                    if (componentKey != null && value is String) map[componentKey] = value
                }
            map
        }
}
