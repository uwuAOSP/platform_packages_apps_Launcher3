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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.launcher3.InvariantDeviceProfile
import com.android.launcher3.LauncherPrefs
import com.android.launcher3.R
import com.android.settingslib.spa.widget.preference.SliderPreference
import com.android.settingslib.spa.widget.preference.SliderPreferenceModel
import com.android.settingslib.spa.widget.ui.Category

private const val MIN_GRID = 3
private const val MAX_GRID = 10

@Composable
fun GridSizeSettingsContent(contentPadding: PaddingValues) {
    val context = LocalContext.current
    val idp = InvariantDeviceProfile.INSTANCE.get(context)
    val prefs = LauncherPrefs.get(context)
    val isFoldable = idp.deviceType == InvariantDeviceProfile.TYPE_MULTI_DISPLAY
    val storedUnfoldedHotseatColumns = prefs.get(LauncherPrefs.HOTSEAT_COLUMNS_UNFOLDED)
    val currentUnfoldedHotseatColumns =
        storedUnfoldedHotseatColumns.coerceAtLeast(idp.numShownHotseatIcons)
    val maxGrid =
        maxOf(MAX_GRID, idp.numColumns, idp.numRows, idp.numShownHotseatIcons,
            currentUnfoldedHotseatColumns)

    var columns by rememberSaveable { mutableIntStateOf(idp.numColumns) }
    var rows by rememberSaveable { mutableIntStateOf(idp.numRows) }
    var hotseatColumns by rememberSaveable { mutableIntStateOf(idp.numShownHotseatIcons) }
    var unfoldedHotseatColumns by rememberSaveable {
        mutableIntStateOf(
            currentUnfoldedHotseatColumns,
        )
    }

    Column(modifier = Modifier.padding(contentPadding).fillMaxSize()) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth().weight(1f)) {
            val isPortrait = LocalConfiguration.current.orientation ==
                android.content.res.Configuration.ORIENTATION_PORTRAIT
            val settingsMinHeight = (maxHeight * if (isPortrait) 0.40f else 0.52f)
                .coerceAtLeast(if (isPortrait) 315.dp else 280.dp)
            val previewMaxHeight = (maxHeight - settingsMinHeight)
                .coerceAtLeast(if (isPortrait) 180.dp else 140.dp)

            Column(modifier = Modifier.fillMaxHeight()) {
                GridOverridesPreview(
                    columns = columns,
                    rows = rows,
                    hotseatColumns = hotseatColumns,
                    hotseatColumnsUnfolded = unfoldedHotseatColumns,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = previewMaxHeight)
                        .padding(horizontal = 16.dp, vertical = if (isPortrait) 16.dp else 12.dp),
                )

                Column(
                    modifier = Modifier.weight(1f).heightIn(min = settingsMinHeight),
                ) {
                    Column(
                        modifier = Modifier.weight(1f).verticalScroll(rememberScrollState()),
                    ) {
                        Category(title = context.getString(R.string.home_screen_grid)) {
                            GridSlider(
                                title = R.string.grid_columns,
                                value = columns,
                                range = MIN_GRID..maxGrid,
                                onValueChange = { columns = it },
                                onValueChangeFinished = {
                                    idp.setGridSize(
                                        rows,
                                        columns,
                                        hotseatColumns,
                                        if (isFoldable) unfoldedHotseatColumns else hotseatColumns,
                                    )
                                },
                            )
                            GridSlider(
                                title = R.string.grid_rows,
                                value = rows,
                                range = MIN_GRID..maxGrid,
                                onValueChange = { rows = it },
                                onValueChangeFinished = {
                                    idp.setGridSize(
                                        rows,
                                        columns,
                                        hotseatColumns,
                                        if (isFoldable) unfoldedHotseatColumns else hotseatColumns,
                                    )
                                },
                            )
                            GridSlider(
                                title = R.string.dock_icons,
                                value = hotseatColumns,
                                range = MIN_GRID..maxGrid,
                                onValueChange = {
                                    hotseatColumns = it
                                    if (isFoldable) {
                                        unfoldedHotseatColumns = unfoldedHotseatColumns.coerceAtLeast(it)
                                    }
                                },
                                onValueChangeFinished = {
                                    idp.setGridSize(
                                        rows,
                                        columns,
                                        hotseatColumns,
                                        if (isFoldable) unfoldedHotseatColumns else hotseatColumns,
                                    )
                                },
                            )
                            if (isFoldable) {
                                GridSlider(
                                    title = R.string.dock_icons_unfolded,
                                    value = unfoldedHotseatColumns,
                                    range = hotseatColumns..maxGrid,
                                     onValueChange = {
                                         unfoldedHotseatColumns = it.coerceAtLeast(hotseatColumns)
                                     },
                                     onValueChangeFinished = {
                                         idp.setGridSize(
                                             rows,
                                             columns,
                                             hotseatColumns,
                                             unfoldedHotseatColumns,
                                         )
                                     },
                                 )
                            }
                        }
                    }

                }
            }
        }
    }
}

@Composable
private fun GridSlider(
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
