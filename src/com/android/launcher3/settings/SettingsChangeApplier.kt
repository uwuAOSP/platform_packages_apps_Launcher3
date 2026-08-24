/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.settings

import android.content.Context
import com.android.launcher3.InvariantDeviceProfile
import com.android.launcher3.Item
import com.android.launcher3.Launcher
import com.android.launcher3.LauncherPrefs

/** Persists a setting and updates an already running launcher immediately. */
internal fun applyLauncherSetting(
    context: Context,
    vararg updates: Pair<Item, Any>,
) {
    LauncherPrefs.get(context).put(*updates)
    InvariantDeviceProfile.INSTANCE.get(context).refreshAfterPreferencesChanged()
    Launcher.ACTIVITY_TRACKER.getCreatedContext<Launcher>()?.recreate()
}
