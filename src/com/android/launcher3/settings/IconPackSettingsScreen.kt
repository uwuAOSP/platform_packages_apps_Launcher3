/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.core.graphics.PathParser
import com.android.launcher3.LauncherAppState
import com.android.launcher3.LauncherPrefs
import com.android.launcher3.R
import com.android.launcher3.InvariantDeviceProfile
import com.android.launcher3.graphics.ThemeManager
import com.android.launcher3.icons.iconpack.IconPackRepository
import com.android.launcher3.shapes.ShapesProvider
import com.android.settingslib.spa.widget.preference.ListPreference
import com.android.settingslib.spa.widget.preference.ListPreferenceModel
import com.android.settingslib.spa.widget.preference.ListPreferenceOption
import com.android.settingslib.spa.widget.preference.Preference
import com.android.settingslib.spa.widget.preference.PreferenceModel
import com.android.settingslib.spa.widget.preference.SwitchPreference
import com.android.settingslib.spa.widget.preference.SwitchPreferenceModel
import com.android.settingslib.spa.widget.ui.Category

@Composable
fun IconSettingsContent(
    contentPadding: PaddingValues,
    selectedPackage: String,
    onSelectedPackageChange: (String) -> Unit,
) {
    val context = LocalContext.current
    val prefs = LauncherPrefs.get(context)
    val themeManager = remember { ThemeManager.INSTANCE.get(context) }
    val shapes = remember { ShapesProvider.iconShapes.toList() }
    val packs = remember { IconPackRepository.getAvailablePacks(context) }
    val idp = remember { InvariantDeviceProfile.INSTANCE.get(context) }
    val initialShapeKey = remember { prefs.get(ThemeManager.PREF_ICON_SHAPE) }
    var appliedShapeKey by rememberSaveable { mutableStateOf(initialShapeKey) }

    Column(
        modifier = Modifier
            .padding(contentPadding)
            .verticalScroll(rememberScrollState()),
    ) {
        Category {
            ThemedIconsPreference(context, themeManager)
            if (shapes.size > 1) {
                IconShapePreference(
                    context = context,
                    shapes = shapes,
                    previewIdp = idp,
                    selectedKey = appliedShapeKey,
                    onApply = { key ->
                        prefs.put(ThemeManager.PREF_ICON_SHAPE, key)
                        appliedShapeKey = key
                        LauncherAppState.getInstance(context).model
                            .reloadIfActive("icon-shape-changed")
                    },
                )
            }
            if (com.android.systemui.shared.Flags.workspaceItemsLabelHidden()) {
                AppLabelsPreference(context, prefs)
            }
            IconPackPreference(context, prefs, packs, selectedPackage, onSelectedPackageChange)
        }
    }
}

@Composable
private fun ThemedIconsPreference(
    context: android.content.Context,
    themeManager: ThemeManager,
) {
    var enabled by remember { mutableStateOf(themeManager.isMonoThemeEnabled) }

    SwitchPreference(
        object : SwitchPreferenceModel {
            override val title = context.getString(R.string.icon_themed_title)
            override val summary = {
                context.getString(
                    if (enabled) R.string.icon_themed_summary_on
                    else R.string.icon_themed_summary_off
                )
            }
            override val checked = { enabled }
            override val onCheckedChange: ((Boolean) -> Unit)? = { value: Boolean ->
                enabled = value
                themeManager.isMonoThemeEnabled = value
                LauncherAppState.getInstance(context).model
                    .reloadIfActive("icon-themed-changed")
            }
        }
    )
}

@Composable
private fun IconShapePreference(
    context: android.content.Context,
    shapes: List<com.android.launcher3.shapes.IconShapeModel>,
    previewIdp: InvariantDeviceProfile,
    selectedKey: String,
    onApply: (String) -> Unit,
) {
    var dialogOpen by rememberSaveable { mutableStateOf(false) }
    var pendingShapeKey by rememberSaveable { mutableStateOf(selectedKey) }

    Preference(
        object : PreferenceModel {
            override val title = context.getString(R.string.icon_shape_title)
            override val summary = {
                shapes.firstOrNull { it.key == selectedKey }?.let {
                    context.getString(it.titleId)
                } ?: context.getString(shapes.first().titleId)
            }
            override val onClick = {
                pendingShapeKey = selectedKey
                dialogOpen = true
            }
        }
    )

    if (dialogOpen) {
        IconShapePickerDialog(
            shapes = shapes,
            previewIdp = previewIdp,
            selectedKey = pendingShapeKey,
            onSelectionChange = { pendingShapeKey = it },
            onApply = {
                onApply(it)
                dialogOpen = false
            },
            onDismiss = {
                dialogOpen = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun IconShapePickerDialog(
    shapes: List<com.android.launcher3.shapes.IconShapeModel>,
    previewIdp: InvariantDeviceProfile,
    selectedKey: String,
    onSelectionChange: (String) -> Unit,
    onApply: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 0.dp,
        dragHandle = { BottomSheetDefaults.DragHandle() },
    ) {
        GridOverridesPreview(
            columns = previewIdp.numColumns,
            rows = previewIdp.numRows,
            hotseatColumns = previewIdp.numShownHotseatIcons,
            iconShapeKey = selectedKey,
            modifier = Modifier
                .fillMaxWidth()
                .height(340.dp)
                .padding(horizontal = 20.dp),
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(shapes, key = { it.key }) { shape ->
                ShapePreviewOption(
                    shape = shape,
                    selected = shape.key == selectedKey,
                    onClick = { onSelectionChange(shape.key) },
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.icon_editor_cancel))
            }
            Button(onClick = { onApply(selectedKey) }) {
                Text(stringResource(R.string.icon_editor_save))
            }
        }
    }
}

@Composable
private fun ShapePreviewOption(
    shape: com.android.launcher3.shapes.IconShapeModel,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val path = remember(shape.pathString) {
        PathParser.createPathFromPathData(shape.pathString).asComposePath()
    }
    val shapeColor = MaterialTheme.colorScheme.onSurfaceVariant
    Surface(
        modifier = Modifier
            .width(64.dp)
            .clickable(onClick = onClick)
            .height(64.dp),
        shape = RoundedCornerShape(if (selected) 24.dp else 32.dp),
        color = if (selected) {
            MaterialTheme.colorScheme.primaryFixedDim
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
    ) {
        Canvas(Modifier.size(64.dp).padding(14.dp)) {
            val scale = minOf(size.width, size.height) / 100f
            withTransform({ scale(scale, scale) }) {
                drawPath(path, color = shapeColor)
            }
        }
    }
}

@Composable
private fun AppLabelsPreference(context: android.content.Context, prefs: LauncherPrefs) {
    var enabled by remember {
        mutableStateOf(!prefs.get(LauncherPrefs.WORKSPACE_ITEMS_LABEL_HIDDEN))
    }

    SwitchPreference(
        object : SwitchPreferenceModel {
            override val title = context.getString(R.string.icon_labels_title)
            override val summary = {
                context.getString(
                    if (enabled) R.string.icon_labels_summary_on
                    else R.string.icon_labels_summary_off
                )
            }
            override val checked = { enabled }
            override val onCheckedChange: ((Boolean) -> Unit)? = { value: Boolean ->
                enabled = value
                prefs.put(LauncherPrefs.WORKSPACE_ITEMS_LABEL_HIDDEN, !value)
                LauncherAppState.getInstance(context).model
                    .reloadIfActive("workspace-label-visibility-changed")
            }
        }
    )
}

@Composable
private fun IconPackPreference(
    context: android.content.Context,
    prefs: LauncherPrefs,
    packs: List<com.android.launcher3.icons.iconpack.IconPackInfo>,
    selectedPackage: String,
    onSelectedPackageChange: (String) -> Unit,
) {
    val selectedIndex = packs.indexOfFirst { it.packageName == selectedPackage } + 1
    val state = remember(selectedPackage, packs) {
        mutableIntStateOf(selectedIndex.coerceAtLeast(0))
    }
    val options = listOf(ListPreferenceOption(0, context.getString(R.string.icon_pack_none))) +
        packs.mapIndexed { index, pack -> ListPreferenceOption(index + 1, pack.label) }

    ListPreference(
        object : ListPreferenceModel {
            override val title = context.getString(R.string.icon_pack_select_title)
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
