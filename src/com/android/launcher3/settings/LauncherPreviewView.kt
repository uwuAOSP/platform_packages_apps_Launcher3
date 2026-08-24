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

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.android.launcher3.InvariantDeviceProfile
import com.android.launcher3.LauncherAppState
import com.android.launcher3.R
import com.android.launcher3.preview.LauncherPreviewRenderer
import com.android.launcher3.util.Executors.MAIN_EXECUTOR
import com.android.launcher3.util.Themes
import kotlin.math.min

@SuppressLint("ViewConstructor")
class LauncherPreviewView(
    context: Context,
    private val idp: InvariantDeviceProfile,
) : FrameLayout(context) {

    private var destroyed = false
    private var rendererStarted = false
    private var rendererView: View? = null
    private var renderer: LauncherPreviewRenderer? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (rendererStarted) return
        rendererStarted = true
        renderer = LauncherPreviewRenderer(
            context,
            0,
            null,
            LauncherAppState.getInstance(context.applicationContext).model,
            Themes.getActivityThemeRes(context),
            idp,
        )
        renderer!!.initialRender.thenAcceptAsync({ view ->
            if (destroyed || view == null) return@thenAcceptAsync
            configureAndAttachView(view)
        }, MAIN_EXECUTOR)
    }

    fun destroy() {
        destroyed = true
        // A renderer with no attached root will not receive BaseContext's detach callback.
        if (rendererView == null) renderer?.onViewDestroyed()
        renderer = null
        rendererView = null
        removeAllViews()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        rendererView?.let(::updateScale)
    }

    private fun configureAndAttachView(view: View) {
        if (destroyed) return
        updateScale(view)
        view.pivotX = if (layoutDirection == LAYOUT_DIRECTION_RTL) {
            view.measuredWidth.toFloat()
        } else {
            0f
        }
        view.pivotY = 0f
        view.layoutParams = LayoutParams(view.measuredWidth, view.measuredHeight)
        rendererView = view
        addView(view)
        requestLayout()
    }

    private fun updateScale(view: View) {
        if (view.measuredWidth == 0 || view.measuredHeight == 0) return
        val scale = min(
            measuredWidth / view.measuredWidth.toFloat(),
            measuredHeight / view.measuredHeight.toFloat(),
        )
        view.scaleX = scale
        view.scaleY = scale
    }
}
