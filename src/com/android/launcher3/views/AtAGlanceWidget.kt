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

package com.android.launcher3.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.android.launcher3.R
import com.android.launcher3.smartspacer.LauncherSmartspacer
import com.android.launcher3.util.LockedUserState

class AtAGlanceWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private lateinit var fallbackView: View
    private var smartspacerView: View? = null
    private val userUnlockedRunnable = Runnable {
        if (isAttachedToWindow) refresh()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        fallbackView = findViewById(R.id.at_a_glance_fallback)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val lockedUserState = LockedUserState.get(context)
        if (!lockedUserState.isUserUnlocked) {
            lockedUserState.runOnUserUnlocked(action = userUnlockedRunnable)
        }
        refresh()
    }

    override fun onDetachedFromWindow() {
        LockedUserState.get(context).removeOnUserUnlockedRunnable(userUnlockedRunnable)
        clearSmartspacerView()
        super.onDetachedFromWindow()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) refresh()
    }

    private fun refresh() {
        if (!LauncherSmartspacer.isEnabled(context) || !LauncherSmartspacer.isInstalled(context)) {
            showFallback()
            return
        }
        // Keep the SDK view visible before permission is granted so Smartspacer can show its
        // permission-request target. The SDK replaces it with regular targets after the user
        // grants access.
        showSmartspacer()
    }

    private fun showSmartspacer() {
        (fallbackView as? FirstPageStatusView)?.setDataSourcesEnabled(false)
        ensureSmartspacerView().visibility = View.VISIBLE
        fallbackView.visibility = View.GONE
    }

    private fun showFallback() {
        clearSmartspacerView()
        (fallbackView as? FirstPageStatusView)?.setDataSourcesEnabled(
            LockedUserState.get(context).isUserUnlocked
        )
        fallbackView.visibility = View.VISIBLE
    }

    private fun ensureSmartspacerView(): View =
        smartspacerView ?: LayoutInflater.from(context)
            .inflate(R.layout.launcher_smartspacer_view, this, false)
            .also {
                addView(it, 0)
                smartspacerView = it
            }

    private fun clearSmartspacerView() {
        smartspacerView?.let {
            removeView(it)
            smartspacerView = null
        }
    }

}
