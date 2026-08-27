/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.icons

import android.content.ComponentName
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.UserHandle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.activity.ComponentActivity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.internal.graphics.drawable.BackgroundBlurDrawable
import com.android.launcher3.LauncherAppState
import com.android.launcher3.LauncherPrefs
import com.android.launcher3.R
import com.android.launcher3.Utilities.shouldReduceWorkspaceBlurUsage
import com.android.launcher3.icons.iconpack.IconPackEntry
import com.android.launcher3.icons.iconpack.IconPackRepository
import com.android.launcher3.icons.iconpack.IconOverrideRepository
import com.android.launcher3.util.ComponentKey
import com.android.launcher3.util.WindowBlurState
import com.android.launcher3.views.DialogTheme
import androidx.core.view.WindowCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
class LauncherIconCustomizeActivity : ComponentActivity() {
    private lateinit var componentKey: ComponentKey
    private lateinit var rootView: FrameLayout
    private var blurDrawable: BackgroundBlurDrawable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.attributes = window.attributes.apply { dimAmount = 0f }

        val component = ComponentName.unflattenFromString(
            intent.getStringExtra(EXTRA_COMPONENT) ?: ""
        ) ?: return finish()
        val user = intent.getParcelableExtra(EXTRA_USER, UserHandle::class.java)
            ?: return finish()
        componentKey = ComponentKey(component, user)

        rootView = FrameLayout(this).apply {
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
        }
        setContentView(rootView)
        rootView.addView(
            ComposeView(this).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
                setContent {
                    DialogTheme {
                        val blurEnabled = !shouldReduceWorkspaceBlurUsage(this@LauncherIconCustomizeActivity) &&
                            WindowBlurState.getInstance(this@LauncherIconCustomizeActivity).value
                        LaunchedEffect(blurEnabled) {
                            if (blurEnabled) {
                                rootView.post { updateBlurBackground(rootView, 1f) }
                            } else {
                                rootView.background = null
                            }
                        }
                        CustomizeSheet(
                            componentKey,
                            onClose = { finish() },
                        )
                    }
                }
            },
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            ),
        )
        rootView.post {
            if (!shouldReduceWorkspaceBlurUsage(this) &&
                WindowBlurState.getInstance(this).value) {
                updateBlurBackground(rootView, 1f)
            }
        }
    }

    private fun updateBlurBackground(view: View, progress: Float) {
        if (blurDrawable == null) {
            blurDrawable = view.viewRootImpl?.createBackgroundBlurDrawable()
        }
        blurDrawable?.apply {
            setBlurRadius(
                (resources.getDimensionPixelSize(R.dimen.popup_blur_radius) *
                    progress.coerceIn(0f, 1f)).toInt()
            )
            setVisible(true, false)
        }
        view.background = blurDrawable
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus && !shouldReduceWorkspaceBlurUsage(this) &&
            WindowBlurState.getInstance(this).value) {
            rootView.post { updateBlurBackground(rootView, 1f) }
        }
    }

    override fun onDestroy() {
        blurDrawable?.setVisible(false, false)
        blurDrawable = null
        super.onDestroy()
    }

    companion object {
        private const val EXTRA_COMPONENT = "component"
        private const val EXTRA_USER = "user"

        @JvmStatic
        fun createIntent(context: Context, key: ComponentKey) =
            android.content.Intent(context, LauncherIconCustomizeActivity::class.java).apply {
                putExtra(EXTRA_COMPONENT, key.componentName.flattenToString())
                putExtra(EXTRA_USER, key.user)
            }
    }
}

@Composable
private fun CustomizeSheet(key: ComponentKey, onClose: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable(onClick = onClose),
        )
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .heightIn(max = 640.dp)
                .animateContentSize()
                .clickable(onClick = {}),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.86f),
            tonalElevation = 0.dp,
        ) {
            CustomizeContent(key, onClose)
        }
    }
}

@Composable
private fun CustomizeContent(key: ComponentKey, onClose: () -> Unit) {
    val context = LocalContext.current
    val defaultTitle = remember {
        context.packageManager.getActivityInfo(key.componentName, 0).loadLabel(
            context.packageManager
        ).toString()
    }
    var title by remember {
        mutableStateOf(IconOverrideRepository.getLabel(context, key) ?: defaultTitle)
    }
    var showIconPicker by rememberSaveable { mutableStateOf(false) }
    val packName = LauncherPrefs.get(context).get(LauncherPrefs.ICON_PACK_PACKAGE)
    var entries by remember(packName) { mutableStateOf<List<IconPackEntry>>(emptyList()) }
    LaunchedEffect(packName, showIconPicker) {
        if (showIconPicker) {
            entries = withContext(Dispatchers.IO) {
                IconPackRepository.getPack(context, packName)?.getAllIcons().orEmpty()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .padding(vertical = 16.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable { showIconPicker = !showIconPicker }
                .padding(8.dp),
        ) {
            val icon = remember {
                runCatching {
                    context.packageManager.getActivityInfo(key.componentName, 0).loadIcon(
                        context.packageManager
                    )
                }.getOrNull()
            }
            DrawablePreview(icon, 54)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = context.getString(R.string.customize_icon),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(4.dp).size(12.dp),
                )
            }
        }
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            singleLine = true,
            label = { Text(context.getString(R.string.icon_name_label)) },
        )
        Text(
            text = key.componentName.flattenToString(),
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (showIconPicker) {
            if (packName.isEmpty()) {
                Text(
                    text = context.getString(R.string.icon_picker_empty),
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(56.dp),
                    modifier = Modifier.fillMaxWidth().height(260.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(entries, key = { it.drawableName }) { entry ->
                        IconPackPreview(entry) {
                            IconOverrideRepository.set(context, key, entry)
                            LauncherAppState.getInstance(context).model
                                .reloadIfActive("icon-customization-changed")
                            showIconPicker = false
                        }
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onClose) {
                Text(context.getString(R.string.icon_editor_cancel))
            }
            Button(onClick = {
                IconOverrideRepository.setLabel(context, key, title)
                LauncherAppState.getInstance(context).model.reloadIfActive("icon-label-changed")
                onClose()
            }) {
                Text(context.getString(R.string.icon_editor_save))
            }
        }
    }
}

@Composable
private fun IconPackPreview(entry: IconPackEntry, onClick: () -> Unit) {
    val context = LocalContext.current
    var drawable by remember(entry) { mutableStateOf<Drawable?>(null) }
    LaunchedEffect(entry) {
        drawable = withContext(Dispatchers.IO) {
            IconPackRepository.getDrawable(
                context,
                entry,
                context.resources.configuration.densityDpi,
            )
        }
    }
    Box(
        modifier = Modifier.size(56.dp).clip(MaterialTheme.shapes.small).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        DrawablePreview(drawable, 48)
    }
}

@Composable
private fun DrawablePreview(drawable: Drawable?, size: Int) {
    val context = LocalContext.current
    AndroidView(
        factory = { ImageView(context).apply { scaleType = ImageView.ScaleType.CENTER_INSIDE } },
        update = { it.setImageDrawable(drawable) },
        modifier = Modifier.size(size.dp),
    )
}
