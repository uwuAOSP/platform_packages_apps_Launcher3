/*
 * Copyright (C) 2026 The uwuAOSP Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.allapps.search

import android.graphics.Rect
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.android.launcher3.AbstractFloatingView
import com.android.launcher3.Insettable
import com.android.launcher3.R
import com.android.launcher3.allapps.ActivityAllAppsContainerView
import com.android.launcher3.views.ActivityContext
import com.android.launcher3.views.BaseDragLayer
import com.android.settingslib.spa.framework.theme.SettingsTheme

/** A launcher-owned keypad; app results remain scrollable and accessible while it is open. */
class T9SearchKeyboard(
    private val input: AppsSearchContainerLayout,
    private val appsView: ActivityAllAppsContainerView<*>,
    private val onDismiss: Runnable,
) : AbstractFloatingView(input.context, null), Insettable {
    private val activity: ActivityContext = ActivityContext.lookupContext(context)
    private val content = ComposeView(context).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
        setViewTreeLifecycleOwner(activity)
        setViewTreeSavedStateRegistryOwner(activity)
        setContent {
            SettingsTheme {
                Keypad(
                    onDigit = ::insertDigit,
                    onClear = { input.setText("") },
                    onDelete = ::deleteCharacter,
                    onHide = { close(false) },
                )
            }
        }
    }

    init {
        orientation = VERTICAL
        addView(content, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT))
        addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            if (mIsOpen) appsView.setSearchKeyboardInset(height)
        }
    }

    fun show() {
        if (isOpen) return
        mIsOpen = true
        activity.dragLayer.addView(this, BaseDragLayer.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ).apply { gravity = Gravity.BOTTOM })
    }

    override fun setInsets(insets: Rect) {
        setPadding(insets.left, 0, insets.right, insets.bottom)
    }

    override fun handleClose(animate: Boolean) {
        if (!mIsOpen) return
        mIsOpen = false
        (parent as? ViewGroup)?.removeView(this)
        appsView.setSearchKeyboardInset(0)
        onDismiss.run()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mIsOpen) {
            mIsOpen = false
            appsView.setSearchKeyboardInset(0)
            onDismiss.run()
        }
    }

    override fun isOfType(type: Int) = type and TYPE_T9_SEARCH_KEYBOARD != 0

    override fun onControllerInterceptTouchEvent(ev: MotionEvent) = false

    private fun insertDigit(digit: String) {
        val text = input.editableText
        val start = input.selectionStart.coerceAtLeast(0)
        val end = input.selectionEnd.coerceAtLeast(0)
        text.replace(minOf(start, end), maxOf(start, end), digit)
        input.setSelection(minOf(start, end) + digit.length)
    }

    private fun deleteCharacter() {
        val text = input.editableText
        val start = input.selectionStart.coerceAtLeast(0)
        val end = input.selectionEnd.coerceAtLeast(0)
        if (start != end) {
            text.delete(minOf(start, end), maxOf(start, end))
        } else if (start > 0) {
            text.delete(Character.offsetByCodePoints(text, start, -1), start)
        }
    }
}

@Composable
private fun Keypad(
    onDigit: (String) -> Unit,
    onClear: () -> Unit,
    onDelete: () -> Unit,
    onHide: () -> Unit,
) {
    Surface(color = MaterialTheme.colorScheme.surfaceContainer, modifier = Modifier.fillMaxWidth()) {
        Box(contentAlignment = Alignment.Center) {
            Column(
                modifier = Modifier.widthIn(max = 420.dp).fillMaxWidth().padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onHide) {
                        Text(stringResource(R.string.app_search_hide_keyboard))
                    }
                }
                val letters = listOf("", "ABC", "DEF", "GHI", "JKL", "MNO", "PQRS", "TUV", "WXYZ")
                repeat(3) { row ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        repeat(3) { column ->
                            val index = row * 3 + column
                            Button(
                                onClick = { onDigit((index + 1).toString()) },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = MaterialTheme.shapes.small,
                                colors = ButtonDefaults.filledTonalButtonColors(),
                            ) {
                                Text((index + 1).toString(), style = MaterialTheme.typography.titleLarge)
                                Text(letters[index], Modifier.padding(start = 6.dp),
                                    style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = onClear, modifier = Modifier.weight(1f).height(48.dp)) {
                        Text(stringResource(R.string.clear_search))
                    }
                    Button(onClick = { onDigit("0") }, modifier = Modifier.weight(1f).height(48.dp),
                        shape = MaterialTheme.shapes.small,
                        colors = ButtonDefaults.filledTonalButtonColors()) { Text("0") }
                    TextButton(onClick = onDelete, modifier = Modifier.weight(1f).height(48.dp)) {
                        Text(stringResource(R.string.app_search_backspace))
                    }
                }
            }
        }
    }
}
