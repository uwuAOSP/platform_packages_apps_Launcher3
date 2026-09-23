/*
 * SPDX-FileCopyrightText: The uwuAOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.launcher3.wallpaper

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.ImageView
import android.widget.LinearLayout
import com.android.launcher3.R

internal class WallpaperCarouselView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {

    private val repository = WallpaperRecentsRepository(context)
    private var isAttached = false
    private var onWallpaperApplied: (() -> Unit)? = null

    init {
        orientation = HORIZONTAL
        gravity = Gravity.CENTER
        setPadding(dp(8), dp(8), dp(8), dp(8))
        visibility = GONE
    }

    fun load(onApplied: () -> Unit) {
        onWallpaperApplied = onApplied
        repository.loadRecentWallpapers { wallpapers ->
            if (parent == null || wallpapers.size < 2) {
                visibility = GONE
            } else {
                removeAllViews()
                wallpapers.forEachIndexed { index, wallpaper ->
                    addWallpaper(wallpaper, index == 0)
                }
                visibility = VISIBLE
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        isAttached = true
    }

    override fun onDetachedFromWindow() {
        isAttached = false
        onWallpaperApplied = null
        super.onDetachedFromWindow()
    }

    private fun addWallpaper(wallpaper: WallpaperRecentsRepository.Wallpaper, selected: Boolean) {
        val image = ImageView(context).apply {
            contentDescription =
                wallpaper.title ?: context.getString(R.string.styles_wallpaper_button_text)
            scaleType = ImageView.ScaleType.CENTER_CROP
            isSelected = selected
            background = GradientDrawable().apply {
                cornerRadius = dp(12).toFloat()
                setColor(if (wallpaper.placeholderColor == 0) Color.DKGRAY else wallpaper.placeholderColor)
            }
            setPadding(dp(2), dp(2), dp(2), dp(2))
            setOnClickListener {
                if (isSelected) return@setOnClickListener
                isEnabled = false
                repository.apply(wallpaper) { success ->
                    if (success) {
                        onWallpaperApplied?.invoke()
                    } else {
                        isEnabled = true
                    }
                }
            }
        }
        addView(
            image,
            LayoutParams(0, dp(64)).apply {
                weight = 1f
                marginStart = dp(3)
                marginEnd = dp(3)
            },
        )
        repository.loadThumbnail(wallpaper) { bitmap ->
            if ((isAttached || image.parent != null) && bitmap != null) image.setImageBitmap(bitmap)
        }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}
