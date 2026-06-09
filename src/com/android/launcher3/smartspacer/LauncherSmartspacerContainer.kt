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

package com.android.launcher3.smartspacer

import android.app.Activity
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID
import android.content.Context
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Process.myUserHandle
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.android.launcher3.Launcher
import com.android.launcher3.LauncherConstants.ActivityCodes.REQUEST_BIND_SMARTSPACER_WIDGET
import com.android.launcher3.LauncherConstants.ActivityCodes.REQUEST_CONFIGURE_SMARTSPACER_WIDGET
import com.android.launcher3.LauncherPrefs
import com.android.launcher3.LauncherPrefs.Companion.SMARTSPACER_WIDGET_ID
import com.android.launcher3.LauncherPrefs.Companion.SMARTSPACER_WIDGET_NEEDS_CONFIG
import com.android.launcher3.LauncherAppState
import com.android.launcher3.R
import com.android.launcher3.dagger.LauncherComponentProvider.appComponent
import com.android.launcher3.views.FirstPageStatusView
import com.android.launcher3.widget.LauncherAppWidgetProviderInfo
import com.android.launcher3.widget.WidgetManagerHelper
import com.android.launcher3.widget.util.WidgetSizeHandler.Companion.updateSizeRanges

class LauncherSmartspacerContainer @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val prefs = LauncherPrefs.get(context)
    private val appWidgetManager = AppWidgetManager.getInstance(context)
    private val widgetManagerHelper = WidgetManagerHelper(context)

    private var widgetView: AppWidgetHostView? = null
    private var currentAction = PendingAction.NONE
    private var isActivityInFlight = false
    private var hasAutoBindPrompted = false
    private var hasAutoConfigurePrompted = false

    private val fallbackView =
        FirstPageStatusView(context).apply {
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        }

    private val actionPrompt =
        TextView(context).apply {
            layoutParams =
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    gravity = Gravity.END or Gravity.BOTTOM
                    marginEnd =
                        resources.getDimensionPixelSize(
                            R.dimen.first_page_pinned_item_horizontal_margin
                        )
                    bottomMargin =
                        resources.getDimensionPixelSize(
                            R.dimen.first_page_pinned_item_vertical_margin
                        )
                }
            visibility = View.GONE
            background = createPromptBackground()
            setPaddingRelative(dpToPx(14), dpToPx(8), dpToPx(14), dpToPx(8))
            setTextColor(context.getColor(R.color.materialColorOnSurface))
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            setOnClickListener { launchCurrentAction() }
        }

    init {
        addView(fallbackView)
        addView(actionPrompt)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        refresh()
    }

    override fun onDetachedFromWindow() {
        isActivityInFlight = false
        super.onDetachedFromWindow()
    }

    override fun onWindowFocusChanged(hasWindowFocus: Boolean) {
        super.onWindowFocusChanged(hasWindowFocus)
        if (hasWindowFocus) {
            isActivityInFlight = false
            refresh()
        }
    }

    fun onSmartspacerActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        isActivityInFlight = false
        when (requestCode) {
            REQUEST_BIND_SMARTSPACER_WIDGET -> handleBindResult(resultCode, data)
            REQUEST_CONFIGURE_SMARTSPACER_WIDGET -> handleConfigureResult(resultCode)
        }
    }

    private fun refresh() {
        val provider = resolveProvider()
        if (provider == null) {
            val storedWidgetId = getStoredWidgetId()
            if (storedWidgetId != INVALID_APPWIDGET_ID) {
                deleteWidgetId(storedWidgetId)
            }
            clearStoredWidget()
            clearWidget(showFallback = false)
            showFallback(PendingAction.NONE)
            return
        }

        val storedWidgetId = getStoredWidgetId()
        val boundInfo = appWidgetManager.getAppWidgetInfo(storedWidgetId)
        val isBoundToProvider = boundInfo?.provider == provider.provider

        if (!isBoundToProvider) {
            if (storedWidgetId != INVALID_APPWIDGET_ID) {
                deleteWidgetId(storedWidgetId)
            }
            clearStoredWidget()
            if (tryBindSilently(provider)) {
                return
            }
            showFallback(PendingAction.BIND, autoLaunch = !hasAutoBindPrompted)
            return
        }

        if (prefs.get(SMARTSPACER_WIDGET_NEEDS_CONFIG) && provider.configure != null) {
            showFallback(PendingAction.CONFIGURE, autoLaunch = !hasAutoConfigurePrompted)
            return
        }

        showWidget(provider, storedWidgetId)
    }

    private fun tryBindSilently(provider: LauncherAppWidgetProviderInfo): Boolean {
        val launcher = getLauncher() ?: return false
        val widgetHolder = launcher.appWidgetHolder ?: return false
        val widgetId = widgetHolder.allocateAppWidgetId()
        if (widgetId == INVALID_APPWIDGET_ID) {
            return false
        }

        val bindAllowed =
            widgetManagerHelper.bindAppWidgetIdIfAllowed(widgetId, provider, createBindOptions())
        if (!bindAllowed) {
            widgetHolder.deleteAppWidgetId(widgetId)
            return false
        }

        storeWidgetId(widgetId)
        if (provider.configure != null) {
            prefs.put(SMARTSPACER_WIDGET_NEEDS_CONFIG, true)
            showFallback(PendingAction.CONFIGURE, autoLaunch = !hasAutoConfigurePrompted)
        } else {
            prefs.put(SMARTSPACER_WIDGET_NEEDS_CONFIG, false)
            showWidget(provider, widgetId)
        }
        return true
    }

    private fun showWidget(provider: LauncherAppWidgetProviderInfo, widgetId: Int) {
        currentAction = PendingAction.NONE
        actionPrompt.visibility = View.GONE
        fallbackView.visibility = View.GONE

        val existingView = widgetView
        if (existingView?.appWidgetId == widgetId && existingView.parent === this) {
            existingView.visibility = View.VISIBLE
            existingView.updateSizeRanges(getSpanX(), 1)
            return
        }

        if (existingView != null) {
            removeView(existingView)
            widgetView = null
        }

        val launcher = getLauncher() ?: run {
            showFallback(PendingAction.NONE)
            return
        }
        val widgetHolder = launcher.appWidgetHolder ?: run {
            showFallback(PendingAction.NONE)
            return
        }

        val newView =
            widgetHolder.createView(widgetId, provider).apply {
                layoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            }
        addView(newView, 0)
        newView.updateSizeRanges(getSpanX(), 1)
        widgetView = newView
    }

    private fun showFallback(action: PendingAction, autoLaunch: Boolean = false) {
        clearWidget(showFallback = true)
        currentAction = action
        fallbackView.visibility = View.VISIBLE
        if (action == PendingAction.NONE) {
            actionPrompt.visibility = View.GONE
        } else {
            actionPrompt.setText(
                when (action) {
                    PendingAction.BIND -> R.string.smartspacer_prompt_setup
                    PendingAction.CONFIGURE -> R.string.smartspacer_prompt_configure
                    PendingAction.NONE -> error("Unexpected action")
                }
            )
            actionPrompt.visibility = View.VISIBLE
        }
        if (autoLaunch) {
            when (action) {
                PendingAction.BIND -> hasAutoBindPrompted = true
                PendingAction.CONFIGURE -> hasAutoConfigurePrompted = true
                PendingAction.NONE -> {}
            }
            post { launchCurrentAction() }
        }
    }

    private fun clearWidget(showFallback: Boolean) {
        widgetView?.let {
            removeView(it)
            widgetView = null
        }
        actionPrompt.visibility = View.GONE
        if (showFallback) {
            fallbackView.visibility = View.VISIBLE
        }
    }

    private fun launchCurrentAction() {
        if (isActivityInFlight) return
        when (currentAction) {
            PendingAction.BIND -> startBindFlow()
            PendingAction.CONFIGURE -> startConfigureFlow()
            PendingAction.NONE -> {}
        }
    }

    private fun startBindFlow() {
        val launcher = getLauncher() ?: return
        val widgetHolder = launcher.appWidgetHolder ?: return
        val provider = resolveProvider() ?: return

        var widgetId = getStoredWidgetId()
        if (appWidgetManager.getAppWidgetInfo(widgetId)?.provider != provider.provider) {
            if (widgetId != INVALID_APPWIDGET_ID) {
                deleteWidgetId(widgetId)
            }
            widgetId = widgetHolder.allocateAppWidgetId()
            if (widgetId == INVALID_APPWIDGET_ID) {
                return
            }
            storeWidgetId(widgetId)
        }

        isActivityInFlight = true
        prefs.put(SMARTSPACER_WIDGET_NEEDS_CONFIG, false)
        widgetHolder.startBindFlow(launcher, widgetId, provider, REQUEST_BIND_SMARTSPACER_WIDGET)
    }

    private fun startConfigureFlow() {
        val launcher = getLauncher() ?: return
        val widgetHolder = launcher.appWidgetHolder ?: return
        val widgetId = getStoredWidgetId()
        if (widgetId == INVALID_APPWIDGET_ID) {
            refresh()
            return
        }

        isActivityInFlight = true
        widgetHolder.startConfigActivity(launcher, widgetId, REQUEST_CONFIGURE_SMARTSPACER_WIDGET)
    }

    private fun handleBindResult(resultCode: Int, data: Intent?) {
        if (resultCode != Activity.RESULT_OK) {
            val widgetId = getStoredWidgetId()
            if (widgetId != INVALID_APPWIDGET_ID) {
                deleteWidgetId(widgetId)
            }
            clearStoredWidget()
            refresh()
            return
        }

        val provider = resolveProvider() ?: run {
            val widgetId = getStoredWidgetId()
            if (widgetId != INVALID_APPWIDGET_ID) {
                deleteWidgetId(widgetId)
            }
            clearStoredWidget()
            refresh()
            return
        }
        val resultWidgetId =
            data?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, getStoredWidgetId())
                ?: getStoredWidgetId()
        if (resultWidgetId != INVALID_APPWIDGET_ID) {
            storeWidgetId(resultWidgetId)
        }

        if (provider.configure != null) {
            prefs.put(SMARTSPACER_WIDGET_NEEDS_CONFIG, true)
            showFallback(PendingAction.CONFIGURE, autoLaunch = !hasAutoConfigurePrompted)
        } else {
            prefs.put(SMARTSPACER_WIDGET_NEEDS_CONFIG, false)
            refresh()
        }
    }

    private fun handleConfigureResult(resultCode: Int) {
        prefs.put(SMARTSPACER_WIDGET_NEEDS_CONFIG, resultCode != Activity.RESULT_OK)
        refresh()
    }

    private fun resolveProvider(): LauncherAppWidgetProviderInfo? {
        return widgetManagerHelper.findProvider(SMARTSPACER_PROVIDER, myUserHandle())
    }

    private fun getStoredWidgetId(): Int {
        return prefs.get(SMARTSPACER_WIDGET_ID)
    }

    private fun storeWidgetId(widgetId: Int) {
        if (getStoredWidgetId() != widgetId) {
            hasAutoConfigurePrompted = false
        }
        prefs.put(SMARTSPACER_WIDGET_ID, widgetId)
    }

    private fun clearStoredWidget() {
        prefs.put(SMARTSPACER_WIDGET_ID, INVALID_APPWIDGET_ID)
        prefs.put(SMARTSPACER_WIDGET_NEEDS_CONFIG, false)
    }

    private fun deleteWidgetId(widgetId: Int) {
        val launcher = getLauncher()
        launcher?.appWidgetHolder?.deleteAppWidgetId(widgetId)
    }

    private fun getLauncher(): Launcher? {
        return try {
            Launcher.getLauncher(context)
        } catch (_: IllegalArgumentException) {
            null
        }
    }

    private fun createPromptBackground(): GradientDrawable {
        val backgroundColor = context.getColor(R.color.materialColorSurfaceContainer)
        return GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = dpToPx(18).toFloat()
            setColor((0xD9 shl 24) or (backgroundColor and 0x00FFFFFF))
        }
    }

    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }

    private fun createBindOptions() =
        context.appComponent.widgetSizeHandler.getWidgetSizeOptions(getSpanX(), 1)

    private fun getSpanX(): Int = LauncherAppState.getIDP(context).numColumns

    private enum class PendingAction {
        NONE,
        BIND,
        CONFIGURE,
    }

    private companion object {
        val SMARTSPACER_PROVIDER =
            android.content.ComponentName(
                "com.kieronquinn.app.smartspacer",
                "com.kieronquinn.app.smartspacer.widgets.SmartspacerAppWidgetProvider",
            )
    }
}
