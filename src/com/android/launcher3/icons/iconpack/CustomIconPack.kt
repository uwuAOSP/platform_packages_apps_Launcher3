/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.icons.iconpack

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException

class CustomIconPack(
    context: Context,
    packageName: String,
) : IconPack(context, packageName) {

    private val resources = context.packageManager.getResourcesForApplication(packageName)
    private val componentMap = mutableMapOf<ComponentName, IconPackEntry>()
    private val allIcons = linkedMapOf<String, IconPackEntry>()
    private val drawableIds = mutableMapOf<String, Int>()

    override val label: String = context.packageManager
        .getApplicationInfo(packageName, 0)
        .loadLabel(context.packageManager)
        .toString()

    override fun getIcon(componentName: ComponentName): IconPackEntry? {
        loadBlocking()
        return componentMap[componentName]
    }

    override fun getDrawable(entry: IconPackEntry, iconDpi: Int): Drawable? {
        loadBlocking()
        val id = getDrawableId(entry.drawableName)
        if (id == 0) return null
        return try {
            resources.getDrawableForDensity(id, iconDpi, null)
        } catch (_: Resources.NotFoundException) {
            null
        }
    }

    override fun getAllIcons(): List<IconPackEntry> {
        loadBlocking()
        return allIcons.values.toList()
    }

    override fun loadInternal() {
        parseAppFilter()
        parseDrawableList()
    }

    private fun parseAppFilter() {
        val parser = getXml("appfilter") ?: return
        try {
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType != XmlPullParser.START_TAG) continue
                if (parser.name != "item") continue

                val component = parser.getAttributeValue(null, "component")
                    ?.removePrefix("ComponentInfo{")
                    ?.removeSuffix("}")
                val drawable = parser.getAttributeValue(null, "drawable") ?: continue
                val componentName = component?.let(ComponentName::unflattenFromString) ?: continue
                val entry = IconPackEntry(packageName, drawable)
                componentMap[componentName] = entry
                allIcons.putIfAbsent(drawable, entry)
            }
        } catch (_: XmlPullParserException) {
        } catch (_: IOException) {
        }
    }

    private fun parseDrawableList() {
        val parser = getXml("drawable") ?: return
        try {
            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.eventType != XmlPullParser.START_TAG || parser.name != "item") continue
                val drawable = parser.getAttributeValue(null, "drawable") ?: continue
                if (getDrawableId(drawable) != 0) {
                    allIcons.putIfAbsent(drawable, IconPackEntry(packageName, drawable))
                }
            }
        } catch (_: XmlPullParserException) {
        } catch (_: IOException) {
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun getDrawableId(name: String): Int = drawableIds.getOrPut(name) {
        resources.getIdentifier(name, "drawable", packageName).takeIf { it != 0 }
            ?: resources.getIdentifier(name, "mipmap", packageName)
    }

    private fun getXml(name: String): XmlPullParser? {
        return try {
            @SuppressLint("DiscouragedApi")
            val id = resources.getIdentifier(name, "xml", packageName)
            if (id != 0) {
                context.packageManager.getXml(packageName, id, null)
            } else {
                Xml.newPullParser().apply {
                    setInput(resources.assets.open("$name.xml"), Charsets.UTF_8.name())
                }
            }
        } catch (_: PackageManager.NameNotFoundException) {
            null
        } catch (_: IOException) {
            null
        } catch (_: XmlPullParserException) {
            null
        }
    }
}
