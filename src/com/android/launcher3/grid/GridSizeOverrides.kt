/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Portions of this file are adapted from LawnchairLauncher/lawnchair's
 * DeviceProfileOverrides (Apache License 2.0).
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
package com.android.launcher3.grid

import android.content.Context
import com.android.launcher3.InvariantDeviceProfile
import com.android.launcher3.LauncherPrefs
import com.android.launcher3.deviceprofile.parser.DeviceTypedMap.INDEX_DEFAULT
import com.android.launcher3.deviceprofile.parser.DeviceTypedMap.INDEX_LANDSCAPE
import com.android.launcher3.deviceprofile.parser.DeviceTypedMap.INDEX_TWO_PANEL_LANDSCAPE
import com.android.launcher3.deviceprofile.parser.DeviceTypedMap.INDEX_TWO_PANEL_PORTRAIT
import com.android.launcher3.deviceprofile.parser.GridOption

// A grid preference value of -1 means "unset": fall back to the selected device profile.
class GridSizeOverrides private constructor(private val prefs: LauncherPrefs) {

    data class GridSize(
        val numRows: Int,
        val numColumns: Int,
        val numHotseatColumns: Int,
    ) {
        val dbFile: String get() = "launcher_${numRows}_${numColumns}_$numHotseatColumns.db"
    }

    fun getGridSize(defaultGrid: GridOption): GridSize {
        return GridSize(
            numRows = resolve(prefs.get(LauncherPrefs.WORKSPACE_ROWS), defaultGrid.numRows),
            numColumns =
                resolve(prefs.get(LauncherPrefs.WORKSPACE_COLUMNS), defaultGrid.numColumns),
            numHotseatColumns =
                resolve(prefs.get(LauncherPrefs.HOTSEAT_COLUMNS), defaultGrid.numHotseatIcons),
        )
    }

    fun applyOverrides(idp: InvariantDeviceProfile, defaultGrid: GridOption) {
        val drawerColumns =
            resolve(prefs.get(LauncherPrefs.DRAWER_COLUMNS), defaultGrid.numAllAppsColumns)
        idp.numAllAppsColumns = drawerColumns
        idp.numDatabaseAllAppsColumns = drawerColumns

        idp.numFolderRows[INDEX_DEFAULT] =
            resolve(prefs.get(LauncherPrefs.FOLDER_ROWS), defaultGrid.numFolderRows[INDEX_DEFAULT])
        idp.numFolderColumns[INDEX_DEFAULT] =
            resolve(
                prefs.get(LauncherPrefs.FOLDER_COLUMNS),
                defaultGrid.numFolderColumns[INDEX_DEFAULT],
            )

        val homeIconSizeFactor = prefs.get(LauncherPrefs.HOME_ICON_SIZE_FACTOR)
        val drawerIconSizeFactor = prefs.get(LauncherPrefs.DRAWER_ICON_SIZE_FACTOR)
        val drawerIconTextSizeFactor = prefs.get(LauncherPrefs.DRAWER_ICON_TEXT_SIZE_FACTOR)
        val sizeIndices =
            intArrayOf(
                INDEX_DEFAULT,
                INDEX_LANDSCAPE,
                INDEX_TWO_PANEL_PORTRAIT,
                INDEX_TWO_PANEL_LANDSCAPE,
            )
        for (index in sizeIndices) {
            idp.iconSize[index] *= homeIconSizeFactor
            idp.allAppsIconSize[index] *= drawerIconSizeFactor
            idp.allAppsIconTextSize[index] *= drawerIconTextSizeFactor
        }

        val hotseatRows = prefs.get(LauncherPrefs.HOTSEAT_ROWS).coerceIn(1, 2)
        val dockPages = prefs.get(LauncherPrefs.DOCK_PAGES).coerceIn(1, 5)
        val requiredSlots = idp.numShownHotseatIcons * hotseatRows * dockPages
        if (idp.numDatabaseHotseatIcons < requiredSlots) {
            idp.numDatabaseHotseatIcons = requiredSlots
        }
    }

    private fun resolve(pref: Int, fallback: Int) = if (pref < 0) fallback else pref

    companion object {
        @JvmStatic
        fun get(context: Context): GridSizeOverrides = GridSizeOverrides(LauncherPrefs.get(context))
    }
}
