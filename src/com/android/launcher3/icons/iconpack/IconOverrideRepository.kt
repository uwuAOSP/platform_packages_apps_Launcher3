/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.icons.iconpack

import android.content.Context
import com.android.launcher3.util.ComponentKey
import java.util.concurrent.ConcurrentHashMap

object IconOverrideRepository {
    private const val PREFS_NAME = "icon_overrides"
    private const val KEY_PREFIX = "override:"
    private const val LABEL_PREFIX = "label:"
    private val maps = ConcurrentHashMap<String, MutableMap<ComponentKey, IconPackEntry>>()
    private val labels = ConcurrentHashMap<String, MutableMap<ComponentKey, String>>()

    fun get(context: Context, key: ComponentKey): IconPackEntry? =
        getMap(context)[key]

    fun set(context: Context, key: ComponentKey, entry: IconPackEntry) {
        getMap(context)[key] = entry
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_PREFIX + key, "${entry.packPackageName}|${entry.drawableName}")
            .apply()
    }

    fun remove(context: Context, key: ComponentKey) {
        getMap(context).remove(key)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_PREFIX + key)
            .apply()
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

    fun getPackageState(context: Context, packageName: String, user: android.os.UserHandle): String =
        (getMap(context)
            .asSequence()
            .filter { it.key.componentName.packageName == packageName && it.key.user == user }
            .sortedBy { it.key.componentName.className }
            .joinToString(";") { (key, value) -> "$key=${value.packPackageName}/${value.drawableName}" } +
            getLabels(context)
                .asSequence()
                .filter { it.key.componentName.packageName == packageName && it.key.user == user }
                .sortedBy { it.key.componentName.className }
                .joinToString(";") { (key, value) -> "$key=$value" })

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
