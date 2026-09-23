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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.android.settingslib.spa.widget.preference.SwitchPreference
import com.android.settingslib.spa.widget.preference.SwitchPreferenceModel
import com.android.settingslib.spa.widget.ui.Category

private const val SMARTSPACER_RELEASES_URI =
    "https://github.com/KieronQuinn/Smartspacer/releases"

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
            SwitchPreference(
                object : SwitchPreferenceModel {
                    override val title = context.getString(R.string.smartspacer_title)
                    override val checked = { useSmartspacer }
                    override val changeable = { smartspacerInstalled }
                    override val onCheckedChange = { checked: Boolean ->
                        useSmartspacer = checked
                        prefs.put(LauncherPrefs.SMARTSPACER_ENABLED.to(checked))
                    }
                }
            )
        }
    }
}

@Composable
private fun AtAGlancePreview(
    useSmartspacer: Boolean,
    smartspacerInstalled: Boolean,
) {
    val context = LocalContext.current
    val previewContext = remember(context) {
        ContextThemeWrapper(context, R.style.AppTheme_DarkText)
    }

    SettingsBanner {
        SettingsBannerContent(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
        ) {
            key(useSmartspacer, smartspacerInstalled) {
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
