/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.icons.iconpack

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import java.util.concurrent.ConcurrentHashMap

object IconPackRepository {
    private val packs = ConcurrentHashMap<String, CustomIconPack?>()

    private val iconPackIntents = listOf(
        Intent("com.novalauncher.THEME"),
        Intent("org.adw.launcher.icons.ACTION_PICK_ICON"),
        Intent("com.dlto.atom.launcher.THEME"),
        Intent(Intent.ACTION_MAIN).addCategory("com.anddoes.launcher.THEME"),
    )

    fun getPack(context: Context, packageName: String): CustomIconPack? {
        if (packageName.isEmpty()) return null
        packs[packageName]?.let { return it }
        val pack = try {
            CustomIconPack(context.applicationContext, packageName)
        } catch (_: PackageManager.NameNotFoundException) {
            return null
        }
        packs.putIfAbsent(packageName, pack)
        return packs[packageName] ?: pack
    }

    fun getDrawable(context: Context, entry: IconPackEntry, iconDpi: Int): Drawable? =
        getPack(context, entry.packPackageName)?.getDrawable(entry, iconDpi)

    fun getAvailablePacks(context: Context): List<IconPackInfo> {
        val pm = context.packageManager
        return iconPackIntents
            .flatMap { pm.queryIntentActivities(it, 0) }
            .map { it.activityInfo.packageName }
            .distinct()
            .mapNotNull { packageName ->
                runCatching {
                    IconPackInfo(
                        packageName,
                        pm.getApplicationInfo(packageName, 0).loadLabel(pm).toString(),
                    )
                }.getOrNull()
            }
            .sortedBy { it.label }
    }
}
