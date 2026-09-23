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
import com.kieronquinn.app.smartspacer.sdk.client.SmartspacerClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AtAGlanceWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val client = SmartspacerClient.getInstance(context)
    private lateinit var fallbackView: View
    private var smartspacerView: View? = null
    private var scope: CoroutineScope? = null
    private var refreshJob: Job? = null
    private val userUnlockedRunnable = Runnable {
        if (isAttachedToWindow) refresh()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        fallbackView = findViewById(R.id.at_a_glance_fallback)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (scope == null) scope = MainScope()
        val lockedUserState = LockedUserState.get(context)
        if (!lockedUserState.isUserUnlocked) {
            lockedUserState.runOnUserUnlocked(action = userUnlockedRunnable)
        }
        refresh()
    }

    override fun onDetachedFromWindow() {
        LockedUserState.get(context).removeOnUserUnlockedRunnable(userUnlockedRunnable)
        refreshJob?.cancel()
        refreshJob = null
        scope?.cancel()
        scope = null
        clearSmartspacerView()
        super.onDetachedFromWindow()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) refresh()
    }

    private fun refresh() {
        val localScope = scope ?: return
        refreshJob?.cancel()
        if (!LauncherSmartspacer.isEnabled(context) || !LauncherSmartspacer.isInstalled(context)) {
            showFallback()
            return
        }
        refreshJob = localScope.launch {
            val permissionState = client.checkCallingPermission()
            if (!isActive) return@launch
            if (permissionState == true) showSmartspacer() else showFallback()
        }
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
