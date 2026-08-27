/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.android.launcher3.LauncherAppState
import com.android.launcher3.LauncherPrefs
import com.android.launcher3.R
import com.android.launcher3.icons.iconpack.IconPackRepository
import com.android.settingslib.spa.widget.preference.ListPreference
import com.android.settingslib.spa.widget.preference.ListPreferenceModel
import com.android.settingslib.spa.widget.preference.ListPreferenceOption
import com.android.settingslib.spa.widget.ui.Category

@Composable
fun IconPackSettingsContent(
    contentPadding: PaddingValues,
    selectedPackage: String,
    onSelectedPackageChange: (String) -> Unit,
) {
    val context = LocalContext.current
    val prefs = LauncherPrefs.get(context)
    val packs = remember { IconPackRepository.getAvailablePacks(context) }
    val selectedIndex = packs.indexOfFirst { it.packageName == selectedPackage } + 1
    val state = remember(selectedPackage, packs) {
        mutableIntStateOf(selectedIndex.coerceAtLeast(0))
    }
    val options = listOf(ListPreferenceOption(0, context.getString(R.string.icon_pack_none))) +
        packs.mapIndexed { index, pack -> ListPreferenceOption(index + 1, pack.label) }

    Column(modifier = Modifier.padding(contentPadding)) {
        Category {
            ListPreference(
                object : ListPreferenceModel {
                    override val title = context.getString(R.string.icon_pack_title)
                    override val options = options
                    override val selectedId = state
                    override val onIdSelected: (Int) -> Unit = { id ->
                        state.intValue = id
                        val packageName = packs.getOrNull(id - 1)?.packageName ?: ""
                        prefs.put(LauncherPrefs.ICON_PACK_PACKAGE, packageName)
                        onSelectedPackageChange(packageName)
                        LauncherAppState.getInstance(context).model
                            .reloadIfActive("icon-pack-changed")
                    }
                }
            )
        }
    }
}
