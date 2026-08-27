/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.icons.iconpack

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Drawable

abstract class IconPack(
    protected val context: Context,
    val packageName: String,
) {
    @Volatile private var loaded = false

    fun loadBlocking() {
        if (loaded) return
        synchronized(this) {
            if (!loaded) {
                loadInternal()
                loaded = true
            }
        }
    }

    abstract val label: String
    abstract fun getIcon(componentName: ComponentName): IconPackEntry?
    abstract fun getDrawable(entry: IconPackEntry, iconDpi: Int): Drawable?
    abstract fun getAllIcons(): List<IconPackEntry>

    protected abstract fun loadInternal()
}
