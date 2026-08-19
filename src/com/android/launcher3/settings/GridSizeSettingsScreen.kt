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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.android.launcher3.InvariantDeviceProfile
import com.android.launcher3.R
import com.android.settingslib.spa.widget.preference.SliderPreference
import com.android.settingslib.spa.widget.preference.SliderPreferenceModel
import com.android.settingslib.spa.widget.ui.Category

private const val MIN_GRID = 3
private const val MAX_GRID = 10

@Composable
fun GridSizeSettingsContent(contentPadding: PaddingValues, onBack: () -> Unit) {
    val context = LocalContext.current
    val idp = InvariantDeviceProfile.INSTANCE.get(context)

    var columns by rememberSaveable { mutableIntStateOf(idp.numColumns) }
    var rows by rememberSaveable { mutableIntStateOf(idp.numRows) }
    var hotseatColumns by rememberSaveable { mutableIntStateOf(idp.numShownHotseatIcons) }

    Column(
        modifier =
            Modifier.padding(contentPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
    ) {
        GridOverridesPreview(
            columns = columns,
            rows = rows,
            hotseatColumns = hotseatColumns,
            modifier = Modifier.fillMaxWidth().height(220.dp).padding(horizontal = 16.dp),
        )

        Category {
            SliderPreference(
                model =
                    object : SliderPreferenceModel {
                        override val title = context.getString(R.string.grid_columns)
                        override val initValue = columns
                        override val valueRange = MIN_GRID..MAX_GRID
                        override val showSteps = true
                        override val onValueChange = { value: Int -> columns = value }
                    }
            )
            SliderPreference(
                model =
                    object : SliderPreferenceModel {
                        override val title = context.getString(R.string.grid_rows)
                        override val initValue = rows
                        override val valueRange = MIN_GRID..MAX_GRID
                        override val showSteps = true
                        override val onValueChange = { value: Int -> rows = value }
                    }
            )
            SliderPreference(
                model =
                    object : SliderPreferenceModel {
                        override val title = context.getString(R.string.dock_icons)
                        override val initValue = hotseatColumns
                        override val valueRange = MIN_GRID..MAX_GRID
                        override val showSteps = true
                        override val onValueChange = { value: Int -> hotseatColumns = value }
                    }
            )
        }

        Button(
            onClick = {
                idp.setGridSize(rows, columns, hotseatColumns)
                onBack()
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Text(stringResource(R.string.action_apply))
        }
    }
}
