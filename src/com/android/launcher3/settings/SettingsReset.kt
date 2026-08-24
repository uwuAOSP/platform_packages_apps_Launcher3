/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.settings

import android.app.Activity
import android.content.Context
import com.android.launcher3.InvariantDeviceProfile
import com.android.launcher3.LauncherPrefs

internal fun resetGridSettings(context: Context) {
    InvariantDeviceProfile.INSTANCE.get(context).resetGridSize()
}

internal fun resetDockSettings(context: Context) {
    applyLauncherSetting(
        context,
        LauncherPrefs.HOTSEAT_ENABLED.to(true),
        LauncherPrefs.HOTSEAT_LABELS.to(false),
        LauncherPrefs.HOTSEAT_ROWS.to(1),
        LauncherPrefs.DOCK_PAGES.to(1),
        LauncherPrefs.HOTSEAT_BOTTOM_FACTOR.to(1.0f),
        LauncherPrefs.HOTSEAT_PAGE_INDICATOR_HEIGHT_FACTOR.to(1.0f),
        LauncherPrefs.HOTSEAT_BACKGROUND_ENABLED.to(false),
        LauncherPrefs.HOTSEAT_BACKGROUND_ALPHA.to(100),
        LauncherPrefs.HOTSEAT_BACKGROUND_CORNER_RADIUS.to(24.0f),
        LauncherPrefs.HOTSEAT_BACKGROUND_INSET_LEFT.to(0),
        LauncherPrefs.HOTSEAT_BACKGROUND_INSET_TOP.to(0),
        LauncherPrefs.HOTSEAT_BACKGROUND_INSET_RIGHT.to(0),
        LauncherPrefs.HOTSEAT_BACKGROUND_INSET_BOTTOM.to(0),
    )
}

internal fun resetSearchSettings(context: Context) {
    applyLauncherSetting(
        context,
        LauncherPrefs.HOTSEAT_MODE.to("lawnchair"),
        LauncherPrefs.HOTSEAT_QSB_PROVIDER.to("google"),
        LauncherPrefs.HOTSEAT_QSB_THEMED.to(true),
        LauncherPrefs.HOTSEAT_QSB_FORCE_WEBSITE.to(false),
        LauncherPrefs.HOTSEAT_QSB_MATCH_DRAWER.to(false),
        LauncherPrefs.HOTSEAT_QSB_CORNER_RADIUS.to(1.0f),
        LauncherPrefs.HOTSEAT_QSB_ALPHA.to(100),
        LauncherPrefs.HOTSEAT_QSB_STROKE_WIDTH.to(0.0f),
    )
}

internal fun resetLayoutSettings(context: Context, kind: LayoutSettingsKind) {
    when (kind) {
        LayoutSettingsKind.Dock -> applyLauncherSetting(
            context,
            LauncherPrefs.HOTSEAT_ROWS.to(1),
            LauncherPrefs.DOCK_PAGES.to(1),
        )
        LayoutSettingsKind.Drawer -> applyLauncherSetting(
            context,
            LauncherPrefs.DRAWER_COLUMNS.to(-1),
        )
        LayoutSettingsKind.Folder -> applyLauncherSetting(
            context,
            LauncherPrefs.FOLDER_ROWS.to(-1),
            LauncherPrefs.FOLDER_COLUMNS.to(-1),
        )
    }
}

internal fun recreateSettings(context: Context) {
    (context as? Activity)?.recreate()
}
