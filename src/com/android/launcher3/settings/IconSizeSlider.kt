/*
 * SPDX-FileCopyrightText: The uwuAOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */
package com.android.launcher3.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import com.android.launcher3.R
import com.android.settingslib.spa.widget.preference.SliderPreference
import com.android.settingslib.spa.widget.preference.SliderPreferenceModel
import kotlin.math.roundToInt

private const val MIN_ICON_SIZE_STEP = 5
private const val MAX_ICON_SIZE_STEP = 15

@Composable
internal fun IconSizeSlider(
    title: Int,
    factor: Float,
    onFactorChange: (Float) -> Unit,
    onValueChangeFinished: (Float) -> Unit,
) {
    val context = LocalContext.current
    val initialStep = (factor * 10).roundToInt().coerceIn(MIN_ICON_SIZE_STEP, MAX_ICON_SIZE_STEP)

    key(initialStep) {
        var value by rememberSaveable { mutableIntStateOf(initialStep) }
        SliderPreference(
            model =
                object : SliderPreferenceModel {
                    override val title =
                        context.getString(
                            R.string.icon_size_percent_title,
                            context.getString(title),
                            value * 10,
                        )
                    override val initValue = initialStep
                    override val valueRange = MIN_ICON_SIZE_STEP..MAX_ICON_SIZE_STEP
                    override val showSteps = true
                    override val onValueChange: (Int) -> Unit = {
                        value = it
                        onFactorChange(it / 10f)
                    }
                    override val onValueChangeFinished: () -> Unit = {
                        onValueChangeFinished(value / 10f)
                    }
                },
        )
    }
}
