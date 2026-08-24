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

package com.android.launcher3.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.launcher3.InvariantDeviceProfile
import com.android.launcher3.LauncherPrefs
import com.android.launcher3.R
import com.android.settingslib.spa.widget.preference.SliderPreference
import com.android.settingslib.spa.widget.preference.SliderPreferenceModel
import com.android.settingslib.spa.widget.ui.Category

private const val MIN_LAYOUT_SIZE = 3
private const val MAX_LAYOUT_SIZE = 20

enum class LayoutSettingsKind {
    Dock,
    Drawer,
    Folder,
}

@Composable
fun LayoutSettingsContent(
    kind: LayoutSettingsKind,
    contentPadding: PaddingValues,
) {
    val context = LocalContext.current
    val prefs = LauncherPrefs.get(context)
    val idp = InvariantDeviceProfile.INSTANCE.get(context)

    val initialValues = when (kind) {
        LayoutSettingsKind.Dock ->
            intArrayOf(
                prefs.get(LauncherPrefs.HOTSEAT_ROWS).coerceIn(1, 2),
                prefs.get(LauncherPrefs.DOCK_PAGES).coerceIn(1, 5),
            )
        LayoutSettingsKind.Drawer ->
            intArrayOf(
                prefs.get(LauncherPrefs.DRAWER_COLUMNS)
                    .takeIf { it >= 0 } ?: idp.numAllAppsColumns,
            )
        LayoutSettingsKind.Folder ->
            intArrayOf(
                prefs.get(LauncherPrefs.FOLDER_ROWS)
                    .takeIf { it >= 0 } ?: idp.numFolderRows[0],
                prefs.get(LauncherPrefs.FOLDER_COLUMNS)
                    .takeIf { it >= 0 } ?: idp.numFolderColumns[0],
            )
    }

    var firstValue by remember { mutableIntStateOf(initialValues[0]) }
    var secondValue by remember {
        mutableIntStateOf(initialValues.getOrElse(1) { initialValues[0] })
    }

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        Category(
            title = context.getString(
                when (kind) {
                    LayoutSettingsKind.Dock -> R.string.dock_settings
                    LayoutSettingsKind.Drawer -> R.string.drawer_settings
                    LayoutSettingsKind.Folder -> R.string.folder_settings
                }
            )
        ) {
            when (kind) {
                LayoutSettingsKind.Dock -> {
                    LayoutSlider(
                        title = R.string.dock_rows,
                        value = firstValue,
                        range = 1..2,
                        onValueChange = { firstValue = it },
                        onValueChangeFinished = {
                            applyLayoutSettings(context, kind, firstValue, secondValue)
                        },
                    )
                    LayoutSlider(
                        title = R.string.dock_pages,
                        value = secondValue,
                        range = 1..5,
                        onValueChange = { secondValue = it },
                        onValueChangeFinished = {
                            applyLayoutSettings(context, kind, firstValue, secondValue)
                        },
                    )
                }
                LayoutSettingsKind.Drawer -> {
                    LayoutSlider(
                        title = R.string.grid_columns,
                        value = firstValue,
                        range = MIN_LAYOUT_SIZE..MAX_LAYOUT_SIZE,
                        onValueChange = { firstValue = it },
                        onValueChangeFinished = {
                            applyLayoutSettings(context, kind, firstValue, secondValue)
                        },
                    )
                }
                LayoutSettingsKind.Folder -> {
                    LayoutSlider(
                        title = R.string.grid_rows,
                        value = firstValue,
                        range = MIN_LAYOUT_SIZE..MAX_LAYOUT_SIZE,
                        onValueChange = { firstValue = it },
                        onValueChangeFinished = {
                            applyLayoutSettings(context, kind, firstValue, secondValue)
                        },
                    )
                    LayoutSlider(
                        title = R.string.grid_columns,
                        value = secondValue,
                        range = MIN_LAYOUT_SIZE..MAX_LAYOUT_SIZE,
                        onValueChange = { secondValue = it },
                        onValueChangeFinished = {
                            applyLayoutSettings(context, kind, firstValue, secondValue)
                        },
                    )
                }
            }
        }

    }
}

@Composable
private fun LayoutSlider(
    title: Int,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    val context = LocalContext.current
    SliderPreference(
        model =
            object : SliderPreferenceModel {
                override val title = context.getString(title)
                override val initValue = value
                override val valueRange = range
                override val showSteps = true
                override val onValueChange = onValueChange
                override val onValueChangeFinished = onValueChangeFinished
            },
    )
}

private fun applyLayoutSettings(
    context: android.content.Context,
    kind: LayoutSettingsKind,
    firstValue: Int,
    secondValue: Int,
) {
    when (kind) {
        LayoutSettingsKind.Dock -> applyLauncherSetting(
            context,
            LauncherPrefs.HOTSEAT_ROWS.to(firstValue),
            LauncherPrefs.DOCK_PAGES.to(secondValue),
        )
        LayoutSettingsKind.Drawer -> applyLauncherSetting(
            context,
            LauncherPrefs.DRAWER_COLUMNS.to(firstValue),
        )
        LayoutSettingsKind.Folder -> applyLauncherSetting(
            context,
            LauncherPrefs.FOLDER_ROWS.to(firstValue),
            LauncherPrefs.FOLDER_COLUMNS.to(secondValue),
        )
    }
}
