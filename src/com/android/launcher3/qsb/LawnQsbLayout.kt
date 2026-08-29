/*
 * Copyright (C) 2026 Lawnchair
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.qsb

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.children
import com.android.launcher3.DeviceProfile
import com.android.launcher3.Launcher
import com.android.launcher3.LauncherPrefs
import com.android.launcher3.R
import com.android.launcher3.logging.StatsLogManager
import com.android.launcher3.settings.SettingsActivity
import com.android.launcher3.views.ActivityContext
import com.android.launcher3.views.OptionsPopupView

class LawnQsbLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val launcher = ActivityContext.lookupContextNoThrow(context) as? Launcher
    private val composeView = ComposeView(context)

    override fun onFinishInflate() {
        super.onFinishInflate()
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
        composeView.setContent {
            val prefs = LauncherPrefs.get(context)
            val provider = QsbSearchProvider.fromId(prefs.get(LauncherPrefs.HOTSEAT_QSB_PROVIDER))
                .takeIf { it.isAvailable(context) }
                ?: QsbSearchProvider.resolveDefault(context)
            val voiceIntent = provider.createVoiceIntent().takeIf {
                provider.supportsVoice && context.canResolve(it)
            }
            val lensIntent = getLensIntent(context).takeIf {
                provider.supportsLens && context.canResolve(it)
            }
            LawnQsbUi(
                state = rememberHotseatQsbState(
                    provider,
                    prefs.get(LauncherPrefs.HOTSEAT_QSB_THEMED),
                    voiceIntent != null,
                    lensIntent != null,
                ),
                style = buildQsbStyle(
                    context,
                    prefs.get(LauncherPrefs.HOTSEAT_QSB_THEMED),
                    prefs.get(LauncherPrefs.HOTSEAT_QSB_ALPHA),
                    ThemesCompat.getQsbFillColor(context),
                    prefs.get(LauncherPrefs.HOTSEAT_QSB_CORNER_RADIUS),
                    prefs.get(LauncherPrefs.HOTSEAT_QSB_STROKE_COLOR),
                    prefs.get(LauncherPrefs.HOTSEAT_QSB_STROKE_WIDTH),
                ),
                actions = QsbActions(
                    onQsbClick = {
                        launcher?.let {
                            if (prefs.get(LauncherPrefs.HOTSEAT_QSB_MATCH_DRAWER)) {
                                it.toggleAllApps(true)
                            } else {
                                provider.launch(it, prefs.get(LauncherPrefs.HOTSEAT_QSB_FORCE_WEBSITE))
                            }
                        }
                    },
                    onQsbLongClick = ::openOptions,
                    onEndIconClick = { id ->
                        runCatching {
                            when (id) {
                                QsbIconId.MIC -> voiceIntent?.let(context::startActivity)
                                QsbIconId.LENS -> lensIntent?.let(context::startActivity)
                                QsbIconId.SEARCH -> Unit
                            }
                        }
                    },
                ),
            )
        }
        addView(composeView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val dp = launcher?.deviceProfile
        if (dp == null || !composeView.isAttachedToWindow) {
            setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec))
            return
        }
        if (!dp.deviceProperties.isPhone) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            return
        }

        val requestedWidth = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        val cellWidth = DeviceProfile.calculateCellWidth(
            requestedWidth,
            dp.workspaceProfile.cellLayoutBorderSpacePx.x,
            dp.hotseatProfile.numShownIcons,
        )
        val iconSize = (dp.workspaceProfile.iconSizePx * 0.92f).toInt()
        val widthReduction = cellWidth - iconSize
        setMeasuredDimension(requestedWidth - widthReduction, height)
        children.forEach { child ->
            measureChildWithMargins(child, widthMeasureSpec, widthReduction, heightMeasureSpec, 0)
        }
    }

    private fun openOptions() {
        val launcher = launcher ?: return
        val position = Rect()
        launcher.dragLayer.getDescendantRectRelativeToSelf(composeView, position)
        OptionsPopupView.show<Launcher>(
            launcher,
            RectF(position),
            listOf(
                OptionsPopupView.OptionItem(
                    context,
                    R.string.action_customize,
                    R.drawable.ic_setting,
                    StatsLogManager.LauncherEvent.IGNORE,
                ) {
                    launcher.startActivity(
                        Intent(launcher, SettingsActivity::class.java).putExtra(
                            SettingsActivity.EXTRA_START_ROUTE,
                            SettingsActivity.SEARCH_ROUTE,
                        )
                    )
                    true
                },
            ),
            true,
        )
    }

    companion object {
        private const val LENS_PACKAGE = "com.google.ar.lens"
        private const val LENS_ACTIVITY =
            "com.google.vr.apps.ornament.app.lens.LensLauncherActivity"

        fun getLensIntent(context: Context): Intent =
            Intent.makeMainActivity(ComponentName(LENS_PACKAGE, LENS_ACTIVITY))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
    }
}

private fun Context.canResolve(intent: Intent): Boolean = packageManager.resolveActivity(intent, 0) != null

private object ThemesCompat {
    fun getQsbFillColor(context: Context): Int =
        if (context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK ==
            Configuration.UI_MODE_NIGHT_YES
        ) {
            0xFF202124.toInt()
        } else {
            Color.WHITE
        }
}
