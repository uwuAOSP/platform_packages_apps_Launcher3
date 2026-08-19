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

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.android.launcher3.BuildConfig
import com.android.launcher3.LauncherFiles
import com.android.launcher3.R
import com.android.launcher3.SessionCommitReceiver
import com.android.launcher3.util.SettingsCache
import com.android.settingslib.spa.widget.preference.Preference
import com.android.settingslib.spa.widget.preference.PreferenceModel
import com.android.settingslib.spa.widget.preference.SwitchPreference
import com.android.settingslib.spa.widget.preference.SwitchPreferenceModel
import com.android.settingslib.spa.widget.scaffold.SettingsScaffold
import com.android.settingslib.spa.widget.ui.Category

private enum class SettingsScreen {
    Main,
    Grid,
}

@Composable
fun LauncherSettingsScreen() {
    val context = LocalContext.current
    var screen by rememberSaveable { mutableStateOf(SettingsScreen.Main) }

    val title =
        if (screen == SettingsScreen.Main) context.getString(R.string.derived_app_name)
        else context.getString(R.string.home_screen_grid)

    SettingsScaffold(title = title) { padding ->
        when (screen) {
            SettingsScreen.Main ->
                MainSettingsContent(contentPadding = padding, onOpenGrid = { screen = SettingsScreen.Grid })
            SettingsScreen.Grid ->
                GridSizeSettingsContent(contentPadding = padding, onBack = { screen = SettingsScreen.Main })
        }
    }
}

@Composable
private fun MainSettingsContent(
    contentPadding: PaddingValues,
    onOpenGrid: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.padding(contentPadding),
    ) {
        Category {
            NotificationDotsPreference(context)

            AddIconsToHomePreference(context)

            Preference(
                model =
                    object : PreferenceModel {
                        override val title = context.getString(R.string.home_screen_grid)
                        override val onClick = onOpenGrid
                    }
            )

            if (BuildConfig.IS_STUDIO_BUILD) {
                Preference(
                    model =
                        object : PreferenceModel {
                            override val title = context.getString(R.string.developer_options_title)
                            override val onClick = {
                                context.startActivity(
                                    Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)
                                )
                            }
                        }
                )
            }
        }
    }
}

@Composable
private fun NotificationDotsPreference(context: Context) {
    val settingsCache = remember { SettingsCache.INSTANCE.get(context) }
    val enabled = remember { mutableStateOf(settingsCache.getValue(SettingsCache.NOTIFICATION_BADGING_URI)) }

    Preference(
        model =
            object : PreferenceModel {
                override val title = context.getString(R.string.notification_dots_title)
                override val summary = {
                    context.getString(
                        if (enabled.value) R.string.notification_dots_desc_on
                        else R.string.notification_dots_desc_off
                    )
                }
                override val onClick = {
                    context.startActivity(Intent(Settings.ACTION_NOTIFICATION_SETTINGS))
                }
            }
    )
}

@Composable
private fun AddIconsToHomePreference(context: Context) {
    val sharedPrefs =
        remember {
            context.getSharedPreferences(LauncherFiles.SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
        }
    var enabled by remember {
        mutableStateOf(sharedPrefs.getBoolean(SessionCommitReceiver.ADD_ICON_PREFERENCE_KEY, true))
    }

    SwitchPreference(
        model =
            object : SwitchPreferenceModel {
                override val title = context.getString(R.string.auto_add_shortcuts_label)
                override val summary = { context.getString(R.string.auto_add_shortcuts_description) }
                override val checked = { enabled }
                override val onCheckedChange = { newChecked: Boolean ->
                    enabled = newChecked
                    sharedPrefs
                        .edit()
                        .putBoolean(SessionCommitReceiver.ADD_ICON_PREFERENCE_KEY, newChecked)
                        .apply()
                }
            }
    )
}
