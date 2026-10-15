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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.launcher3.LauncherPrefs
import com.android.launcher3.R
import com.android.launcher3.smartspacer.LauncherSmartspacer
import com.android.settingslib.spa.framework.compose.LifecycleEffect
import com.android.settingslib.spa.widget.banner.BannerButton
import com.android.settingslib.spa.widget.banner.BannerModel
import com.android.settingslib.spa.widget.banner.SettingsBanner
import com.android.settingslib.spa.widget.banner.SettingsBannerContent
import com.android.settingslib.spa.widget.preference.MainSwitchPreference
import com.android.settingslib.spa.widget.preference.ListPreferenceModel
import com.android.settingslib.spa.widget.preference.ListPreferenceOption
import com.android.settingslib.spa.widget.preference.Preference
import com.android.settingslib.spa.widget.preference.PreferenceModel
import com.android.settingslib.spa.widget.preference.RadioPreferences
import com.android.settingslib.spa.widget.preference.SwitchPreferenceModel
import com.android.settingslib.spa.widget.ui.Category
import kotlinx.coroutines.launch

private const val SMARTSPACER_RELEASES_URI =
    "https://github.com/KieronQuinn/Smartspacer/releases"
private const val UWU_LAUNCHER_PROVIDER_ID = 0
private const val SMARTSPACER_PROVIDER_ID = 1

@Composable
fun AtAGlanceSettingsContent(contentPadding: PaddingValues) {
    val context = LocalContext.current
    val prefs = remember { LauncherPrefs.get(context) }
    var showAtAGlance by remember {
        mutableStateOf(prefs.get(LauncherPrefs.SHOW_AT_A_GLANCE))
    }
    var useSmartspacer by remember {
        mutableStateOf(prefs.get(LauncherPrefs.SMARTSPACER_ENABLED))
    }
    var smartspacerInstalled by remember {
        mutableStateOf(LauncherSmartspacer.isInstalled(context))
    }
    var installPromptDismissed by remember {
        mutableStateOf(prefs.get(LauncherPrefs.SMARTSPACER_INSTALL_PROMPT_DISMISSED))
    }
    var showProviderPicker by remember { mutableStateOf(false) }

    LifecycleEffect(
        onStart = {
            smartspacerInstalled = LauncherSmartspacer.isInstalled(context)
        },
    )

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
    ) {
        MainSwitchPreference(
            object : SwitchPreferenceModel {
                override val title = context.getString(R.string.at_a_glance_switch_title)
                override val checked = { showAtAGlance }
                override val onCheckedChange = { checked: Boolean ->
                    showAtAGlance = checked
                    prefs.put(LauncherPrefs.SHOW_AT_A_GLANCE.to(checked))
                }
            }
        )

        AtAGlancePreview(
            useSmartspacer = useSmartspacer,
            smartspacerInstalled = smartspacerInstalled,
        )

        if (!smartspacerInstalled && !installPromptDismissed) {
            SmartspacerInstallBanner(
                onInstall = { openSmartspacerReleases(context) },
                onDismiss = {
                    installPromptDismissed = true
                    prefs.put(LauncherPrefs.SMARTSPACER_INSTALL_PROMPT_DISMISSED.to(true))
                },
            )
        }

        Category {
            Preference(
                object : PreferenceModel {
                    override val title = context.getString(R.string.at_a_glance_provider_title)
                    override val summary = {
                        context.getString(
                            if (useSmartspacer) {
                                R.string.at_a_glance_provider_smartspacer
                            } else {
                                R.string.at_a_glance_provider_uwu_launcher
                            }
                        )
                    }
                    override val onClick = { showProviderPicker = true }
                }
            )
        }

        if (showProviderPicker) {
            AtAGlanceProviderPickerSheet(
                useSmartspacer = useSmartspacer,
                onProviderSelected = { selectedSmartspacer ->
                    useSmartspacer = selectedSmartspacer
                    prefs.put(LauncherPrefs.SMARTSPACER_ENABLED.to(selectedSmartspacer))
                    showProviderPicker = false
                },
                onDismiss = { showProviderPicker = false },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AtAGlanceProviderPickerSheet(
    useSmartspacer: Boolean,
    onProviderSelected: (Boolean) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val selectedProviderId = remember(useSmartspacer) {
        mutableIntStateOf(if (useSmartspacer) SMARTSPACER_PROVIDER_ID else UWU_LAUNCHER_PROVIDER_ID)
    }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val options = remember(context) {
        listOf(
            ListPreferenceOption(
                id = UWU_LAUNCHER_PROVIDER_ID,
                text = context.getString(R.string.at_a_glance_provider_uwu_launcher),
                summary = context.getString(R.string.at_a_glance_provider_uwu_launcher_summary),
            ),
            ListPreferenceOption(
                id = SMARTSPACER_PROVIDER_ID,
                text = context.getString(R.string.at_a_glance_provider_smartspacer),
                summary = context.getString(R.string.at_a_glance_provider_smartspacer_summary),
            ),
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        RadioPreferences(
            object : ListPreferenceModel {
                override val title = context.getString(R.string.at_a_glance_provider_title)
                override val options = options
                override val selectedId = selectedProviderId
                override val onIdSelected: (Int) -> Unit = { id ->
                    selectedProviderId.intValue = id
                    coroutineScope.launch {
                        sheetState.hide()
                        onProviderSelected(id == SMARTSPACER_PROVIDER_ID)
                    }
                }
            }
        )
    }
}

@Composable
private fun AtAGlancePreview(
    useSmartspacer: Boolean,
    smartspacerInstalled: Boolean,
) {
    val context = LocalContext.current
    val isDarkTheme = isSystemInDarkTheme()
    val previewContext = remember(context, isDarkTheme) {
        ContextThemeWrapper(
            context,
            if (isDarkTheme) R.style.AppTheme_Dark else R.style.AppTheme_DarkText,
        )
    }

    SettingsBanner {
        SettingsBannerContent(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ) {
            key(useSmartspacer, smartspacerInstalled, isDarkTheme) {
                AndroidView(
                    factory = {
                        LayoutInflater.from(previewContext).inflate(
                            R.layout.search_container_at_a_glance,
                            null,
                            false,
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(112.dp)
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                )
            }
        }
    }
}

@Composable
private fun SmartspacerInstallBanner(
    onInstall: () -> Unit,
    onDismiss: () -> Unit,
) {
    SettingsBanner(
        BannerModel(
            title = stringResource(R.string.smartspacer_install_title),
            text = stringResource(R.string.smartspacer_install_summary),
            imageVector = Icons.Outlined.Download,
            onDismiss = onDismiss,
            buttons = listOf(
                BannerButton(
                    text = stringResource(R.string.smartspacer_install_action),
                    onClick = onInstall,
                )
            ),
        )
    )
}

private fun openSmartspacerReleases(context: Context) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(SMARTSPACER_RELEASES_URI)))
    } catch (_: ActivityNotFoundException) {
        // There is no useful fallback when the device has no browser.
    }
}
