/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.android.launcher3.LauncherPrefs
import com.android.launcher3.R
import com.android.launcher3.qsb.QsbSearchProvider
import com.android.settingslib.spa.widget.preference.ListPreference
import com.android.settingslib.spa.widget.preference.ListPreferenceModel
import com.android.settingslib.spa.widget.preference.ListPreferenceOption
import com.android.settingslib.spa.widget.preference.MainSwitchPreference
import com.android.settingslib.spa.widget.preference.SliderPreference
import com.android.settingslib.spa.widget.preference.SliderPreferenceModel
import com.android.settingslib.spa.widget.preference.SwitchPreference
import com.android.settingslib.spa.widget.preference.SwitchPreferenceModel
import com.android.settingslib.spa.widget.scaffold.SettingsPager
import com.android.settingslib.spa.widget.ui.Category

private class DockSettingsState(prefs: LauncherPrefs) {
    val enabled = mutableStateOf(prefs.get(LauncherPrefs.HOTSEAT_ENABLED))
    val rows = mutableIntStateOf(prefs.get(LauncherPrefs.HOTSEAT_ROWS).coerceIn(1, 2))
    val pages = mutableIntStateOf(prefs.get(LauncherPrefs.DOCK_PAGES).coerceIn(1, 5))
    val bottomFactor = mutableIntStateOf(
        (prefs.get(LauncherPrefs.HOTSEAT_BOTTOM_FACTOR) * 100).toInt().coerceIn(0, 170)
    )
    val indicatorFactor = mutableIntStateOf(
        (prefs.get(LauncherPrefs.HOTSEAT_PAGE_INDICATOR_HEIGHT_FACTOR) * 100)
            .toInt().coerceIn(0, 100)
    )
    val backgroundEnabled = mutableStateOf(prefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_ENABLED))
    val backgroundAlpha = mutableIntStateOf(
        prefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_ALPHA).coerceIn(5, 100)
    )
    val backgroundRadius = mutableIntStateOf(
        prefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_CORNER_RADIUS).toInt().coerceIn(0, 100)
    )
    val insetLeft = mutableIntStateOf(
        prefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_INSET_LEFT).coerceIn(0, 100)
    )
    val insetTop = mutableIntStateOf(
        prefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_INSET_TOP).coerceIn(0, 100)
    )
    val insetRight = mutableIntStateOf(
        prefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_INSET_RIGHT).coerceIn(0, 100)
    )
    val insetBottom = mutableIntStateOf(
        prefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_INSET_BOTTOM).coerceIn(0, 100)
    )
    val searchEnabled = mutableStateOf(
        prefs.get(LauncherPrefs.HOTSEAT_MODE) != "disabled"
    )
    val providerIndex = mutableIntStateOf(
        QsbSearchProvider.entries.indexOf(
            QsbSearchProvider.fromId(prefs.get(LauncherPrefs.HOTSEAT_QSB_PROVIDER))
        ).coerceAtLeast(0)
    )
    val qsbThemed = mutableStateOf(prefs.get(LauncherPrefs.HOTSEAT_QSB_THEMED))
    val qsbForceWebsite = mutableStateOf(prefs.get(LauncherPrefs.HOTSEAT_QSB_FORCE_WEBSITE))
    val qsbMatchDrawer = mutableStateOf(prefs.get(LauncherPrefs.HOTSEAT_QSB_MATCH_DRAWER))
    val qsbRadius = mutableIntStateOf(
        (prefs.get(LauncherPrefs.HOTSEAT_QSB_CORNER_RADIUS) * 100).toInt().coerceIn(0, 100)
    )
    val qsbAlpha = mutableIntStateOf(prefs.get(LauncherPrefs.HOTSEAT_QSB_ALPHA).coerceIn(0, 100))
    val qsbStrokeWidth = mutableIntStateOf(
        prefs.get(LauncherPrefs.HOTSEAT_QSB_STROKE_WIDTH).toInt().coerceIn(0, 10)
    )
}

@Composable
fun DockSettingsContent(contentPadding: PaddingValues) {
    val context = LocalContext.current
    val prefs = LauncherPrefs.get(context)
    val state = remember { DockSettingsState(prefs) }

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxWidth(),
    ) {
        MainSwitchPreference(
            object : SwitchPreferenceModel {
                override val title = context.getString(R.string.show_dock)
                override val checked = { state.enabled.value }
                override val onCheckedChange = { enabled: Boolean ->
                    state.enabled.value = enabled
                    saveDockSettings(context, prefs, state)
                }
            }
        )
        if (state.enabled.value) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                SettingsPager(
                    titles = listOf(
                        context.getString(R.string.dock_layout_section),
                        context.getString(R.string.dock_appearance_section),
                    )
                ) { page ->
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    ) {
                        when (page) {
                            0 -> DockLayoutPreferences(context, prefs, state)
                            else -> Category {
                                DockBackgroundPreferences(context, prefs, state)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchSettingsContent(contentPadding: PaddingValues) {
    val context = LocalContext.current
    val prefs = LauncherPrefs.get(context)
    val state = remember { DockSettingsState(prefs) }

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        MainSwitchPreference(
            object : SwitchPreferenceModel {
                override val title = context.getString(R.string.show_dock_search)
                override val checked = { state.searchEnabled.value }
                override val onCheckedChange = { enabled: Boolean ->
                    state.searchEnabled.value = enabled
                    saveSearchSettings(context, prefs, state)
                }
            }
        )
        if (state.searchEnabled.value) {
            Category {
                DockSearchPreferences(context, prefs, state)
            }
        }
    }
}

@Composable
private fun DockLayoutPreferences(
    context: android.content.Context,
    prefs: LauncherPrefs,
    state: DockSettingsState,
) {
    Category {
        ListPreference(
            object : ListPreferenceModel {
                override val title = context.getString(R.string.dock_rows)
                override val options = listOf(
                    ListPreferenceOption(1, "1"),
                    ListPreferenceOption(2, "2"),
                )
                override val selectedId = state.rows
                override val onIdSelected: (Int) -> Unit = {
                    state.rows.intValue = it
                    saveDockSettings(context, prefs, state)
                }
            }
        )
        DockSlider(
            R.string.dock_pages, state.pages.intValue, 1..5,
            onValueChange = { state.pages.intValue = it },
            valueTitleRes = R.string.dock_pages_value,
            showStepsEnabled = true,
            onValueChangeFinished = { saveDockSettings(context, prefs, state) },
        )
        DockSlider(
            R.string.dock_bottom_spacing, state.bottomFactor.intValue, 0..170,
            onValueChange = { state.bottomFactor.intValue = it },
            onValueChangeFinished = { saveDockSettings(context, prefs, state) },
        )
        DockSlider(
            R.string.dock_page_indicator_spacing,
            state.indicatorFactor.intValue,
            0..100,
            onValueChange = { state.indicatorFactor.intValue = it },
            onValueChangeFinished = { saveDockSettings(context, prefs, state) },
        )
    }
}

@Composable
private fun DockBackgroundPreferences(
    context: android.content.Context,
    prefs: LauncherPrefs,
    state: DockSettingsState,
) {
    DockSwitch(R.string.dock_background, state.backgroundEnabled.value) {
        state.backgroundEnabled.value = it
        saveDockSettings(context, prefs, state)
    }
    if (!state.backgroundEnabled.value) return
    DockSlider(
        R.string.dock_background_opacity, state.backgroundAlpha.intValue, 5..100,
        onValueChange = { state.backgroundAlpha.intValue = it },
        onValueChangeFinished = { saveDockSettings(context, prefs, state) },
    )
    DockSlider(
        R.string.dock_background_corner_radius, state.backgroundRadius.intValue, 0..100,
        onValueChange = { state.backgroundRadius.intValue = it },
        onValueChangeFinished = { saveDockSettings(context, prefs, state) },
    )
    DockSlider(
        R.string.dock_background_inset_left, state.insetLeft.intValue, 0..100,
        onValueChange = { state.insetLeft.intValue = it },
        onValueChangeFinished = { saveDockSettings(context, prefs, state) },
    )
    DockSlider(
        R.string.dock_background_inset_top, state.insetTop.intValue, 0..100,
        onValueChange = { state.insetTop.intValue = it },
        onValueChangeFinished = { saveDockSettings(context, prefs, state) },
    )
    DockSlider(
        R.string.dock_background_inset_right, state.insetRight.intValue, 0..100,
        onValueChange = { state.insetRight.intValue = it },
        onValueChangeFinished = { saveDockSettings(context, prefs, state) },
    )
    DockSlider(
        R.string.dock_background_inset_bottom, state.insetBottom.intValue, 0..100,
        onValueChange = { state.insetBottom.intValue = it },
        onValueChangeFinished = { saveDockSettings(context, prefs, state) },
    )
}

@Composable
private fun DockSearchPreferences(
    context: android.content.Context,
    prefs: LauncherPrefs,
    state: DockSettingsState,
) {
    ListPreference(
        object : ListPreferenceModel {
            override val title = context.getString(R.string.search_provider)
            override val options = QsbSearchProvider.entries.mapIndexed { index, provider ->
                ListPreferenceOption(index, context.getString(provider.nameRes))
            }
            override val selectedId = state.providerIndex
            override val onIdSelected: (Int) -> Unit = {
                state.providerIndex.intValue = it
                saveSearchSettings(context, prefs, state)
            }
        }
    )
    DockSwitch(R.string.apply_accent_color_label, state.qsbThemed.value) {
        state.qsbThemed.value = it
        saveSearchSettings(context, prefs, state)
    }
    DockSwitch(R.string.force_website_search, state.qsbForceWebsite.value) {
        state.qsbForceWebsite.value = it
        saveSearchSettings(context, prefs, state)
    }
    DockSwitch(R.string.match_drawer_search, state.qsbMatchDrawer.value) {
        state.qsbMatchDrawer.value = it
        saveSearchSettings(context, prefs, state)
    }
    DockSlider(
        R.string.corner_radius_label, state.qsbRadius.intValue, 0..100,
        onValueChange = { state.qsbRadius.intValue = it },
        onValueChangeFinished = { saveSearchSettings(context, prefs, state) },
    )
    DockSlider(
        R.string.qsb_hotseat_background_transparency, state.qsbAlpha.intValue, 0..100,
        onValueChange = { state.qsbAlpha.intValue = it },
        onValueChangeFinished = { saveSearchSettings(context, prefs, state) },
    )
    DockSlider(
        R.string.qsb_hotseat_stroke_width, state.qsbStrokeWidth.intValue, 0..10,
        onValueChange = { state.qsbStrokeWidth.intValue = it },
        onValueChangeFinished = { saveSearchSettings(context, prefs, state) },
    )
}

@Composable
private fun DockSwitch(titleRes: Int, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val context = LocalContext.current
    SwitchPreference(
        object : SwitchPreferenceModel {
            override val title = context.getString(titleRes)
            override val checked = { checked }
            override val onCheckedChange = onCheckedChange
        }
    )
}

@Composable
private fun DockSlider(
    titleRes: Int,
    value: Int,
    range: IntRange,
    onValueChange: (Int) -> Unit,
    valueTitleRes: Int? = null,
    showStepsEnabled: Boolean = false,
    onValueChangeFinished: () -> Unit,
) {
    val context = LocalContext.current
    SliderPreference(
        object : SliderPreferenceModel {
            override val title = valueTitleRes?.let { context.getString(it, value) }
                ?: context.getString(titleRes)
            override val initValue = value
            override val valueRange = range
            override val showSteps = showStepsEnabled
            override val onValueChange = onValueChange
            override val onValueChangeFinished = onValueChangeFinished
        }
    )
}

private fun saveDockSettings(
    context: android.content.Context,
    prefs: LauncherPrefs,
    state: DockSettingsState,
) {
    applyLauncherSetting(
        context,
        LauncherPrefs.HOTSEAT_ENABLED.to(state.enabled.value),
        LauncherPrefs.HOTSEAT_ROWS.to(state.rows.intValue),
        LauncherPrefs.DOCK_PAGES.to(state.pages.intValue),
        LauncherPrefs.HOTSEAT_BOTTOM_FACTOR.to(state.bottomFactor.intValue / 100f),
        LauncherPrefs.HOTSEAT_PAGE_INDICATOR_HEIGHT_FACTOR.to(
            state.indicatorFactor.intValue / 100f
        ),
        LauncherPrefs.HOTSEAT_BACKGROUND_ENABLED.to(state.backgroundEnabled.value),
        LauncherPrefs.HOTSEAT_BACKGROUND_ALPHA.to(state.backgroundAlpha.intValue),
        LauncherPrefs.HOTSEAT_BACKGROUND_CORNER_RADIUS.to(state.backgroundRadius.intValue.toFloat()),
        LauncherPrefs.HOTSEAT_BACKGROUND_INSET_LEFT.to(state.insetLeft.intValue),
        LauncherPrefs.HOTSEAT_BACKGROUND_INSET_TOP.to(state.insetTop.intValue),
        LauncherPrefs.HOTSEAT_BACKGROUND_INSET_RIGHT.to(state.insetRight.intValue),
        LauncherPrefs.HOTSEAT_BACKGROUND_INSET_BOTTOM.to(state.insetBottom.intValue),
    )
}

private fun saveSearchSettings(
    context: android.content.Context,
    prefs: LauncherPrefs,
    state: DockSettingsState,
) {
    applyLauncherSetting(
        context,
        LauncherPrefs.HOTSEAT_MODE.to(if (state.searchEnabled.value) "lawnchair" else "disabled"),
        LauncherPrefs.HOTSEAT_QSB_PROVIDER.to(
            QsbSearchProvider.entries[state.providerIndex.intValue].id
        ),
        LauncherPrefs.HOTSEAT_QSB_THEMED.to(state.qsbThemed.value),
        LauncherPrefs.HOTSEAT_QSB_FORCE_WEBSITE.to(state.qsbForceWebsite.value),
        LauncherPrefs.HOTSEAT_QSB_MATCH_DRAWER.to(state.qsbMatchDrawer.value),
        LauncherPrefs.HOTSEAT_QSB_CORNER_RADIUS.to(state.qsbRadius.intValue / 100f),
        LauncherPrefs.HOTSEAT_QSB_ALPHA.to(state.qsbAlpha.intValue),
        LauncherPrefs.HOTSEAT_QSB_STROKE_WIDTH.to(state.qsbStrokeWidth.intValue.toFloat()),
    )
}
