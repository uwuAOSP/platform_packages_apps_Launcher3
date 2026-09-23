/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.launcher3.smartspacer

import android.content.Context
import android.content.pm.PackageManager.NameNotFoundException
import com.android.launcher3.LauncherPrefs
import com.android.launcher3.util.LockedUserState
import com.kieronquinn.app.smartspacer.sdk.SmartspacerConstants.SMARTSPACER_PACKAGE_NAME

object LauncherSmartspacer {

    @JvmStatic
    fun isInstalled(context: Context): Boolean = try {
        context.packageManager.getApplicationInfo(SMARTSPACER_PACKAGE_NAME, 0).enabled
    } catch (_: NameNotFoundException) {
        false
    }

    @JvmStatic
    fun isEnabled(context: Context): Boolean =
        LockedUserState.get(context).isUserUnlocked &&
            LauncherPrefs.get(context).get(LauncherPrefs.SMARTSPACER_ENABLED)

    @JvmStatic
    fun isFirstPageStatusEnabled(context: Context): Boolean =
        if (LockedUserState.get(context).isUserUnlocked) {
            LauncherPrefs.get(context).get(LauncherPrefs.SHOW_AT_A_GLANCE)
        } else {
            LauncherPrefs.SHOW_AT_A_GLANCE.defaultValue
        }
}
