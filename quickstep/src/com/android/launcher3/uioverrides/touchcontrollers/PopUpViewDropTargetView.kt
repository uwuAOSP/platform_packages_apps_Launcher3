/*
 * Copyright (C) 2026 The Android Open Source Project
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

package com.android.launcher3.uioverrides.touchcontrollers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import com.android.launcher3.R
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

class PopUpViewDropTargetView
@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val guideRect = Rect()
    private val backgroundDrawable =
        GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
        }
    private val content =
        LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            background = backgroundDrawable
            clipToOutline = true
            val horizontalPadding =
                resources.getDimensionPixelSize(R.dimen.popup_view_drop_target_content_padding_horizontal)
            val verticalPadding =
                resources.getDimensionPixelSize(R.dimen.popup_view_drop_target_content_padding_vertical)
            setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding)
        }
    private val icon =
        ImageView(context).apply {
            setImageResource(R.drawable.ic_view_carousel)
            imageTintList = resources.getColorStateList(R.color.materialColorOnSurface, context.theme)
        }
    private val label =
        TextView(context).apply {
            gravity = Gravity.CENTER_VERTICAL
            textSize = 14f
            setTextColor(context.getColor(R.color.materialColorOnSurface))
        }

    init {
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
        alpha = 0f
        visibility = View.GONE
        val iconSize = resources.getDimensionPixelSize(R.dimen.popup_view_drop_target_icon_size)
        content.addView(
            icon,
            LinearLayout.LayoutParams(iconSize, iconSize).apply {
                marginEnd =
                    resources.getDimensionPixelSize(R.dimen.popup_view_drop_target_icon_margin_end)
            },
        )
        content.addView(
            label,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ),
        )
        addView(
            content,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
        )
    }

    fun attachIfNeeded(parent: FrameLayout) {
        if (this.parent === parent) return
        (this.parent as? FrameLayout)?.removeView(this)
        parent.addView(
            this,
            FrameLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT,
                Gravity.TOP or Gravity.START,
            ),
        )
    }

    fun detachIfNeeded() {
        (parent as? FrameLayout)?.removeView(this)
    }

    fun show() {
        if (visibility != View.VISIBLE) {
            visibility = View.VISIBLE
        }
    }

    fun hide(animated: Boolean) {
        if (!animated) {
            animate().cancel()
            alpha = 0f
            visibility = View.GONE
            return
        }
        animate().cancel()
        animate()
            .alpha(0f)
            .setDuration(HIDE_DURATION)
            .setListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE
                        animate().setListener(null)
                    }
                },
            )
            .start()
    }

    fun updateLayoutForContainer(
        containerWidth: Int,
        containerHeight: Int,
        taskBounds: Rect,
        progress: Float,
        armed: Boolean,
    ) {
        if (containerWidth <= 0 || containerHeight <= 0) {
            return
        }
        val boundedProgress = progress.coerceIn(0f, 1f)
        val sideMargin =
            resources.getDimensionPixelSize(R.dimen.popup_view_drop_target_side_margin)
        val topMarginPx =
            resources.getDimensionPixelSize(R.dimen.popup_view_drop_target_top_margin)
        val minWidth =
            resources.getDimensionPixelSize(R.dimen.popup_view_drop_target_min_width)
        val maxWidth = containerWidth - (sideMargin * 2)
        val width =
            min(
                maxWidth,
                max(
                    minWidth,
                    (containerWidth * (MIN_GUIDE_WIDTH_RATIO +
                        ((MAX_GUIDE_WIDTH_RATIO - MIN_GUIDE_WIDTH_RATIO) * boundedProgress)))
                        .roundToInt(),
                ),
            )
        val aspectRatio =
            if (!taskBounds.isEmpty && taskBounds.height() > 0) {
                (taskBounds.width().toFloat() / taskBounds.height().toFloat())
                    .coerceIn(MIN_GUIDE_ASPECT_RATIO, MAX_GUIDE_ASPECT_RATIO)
            } else {
                DEFAULT_TASK_ASPECT_RATIO
            }
        val height =
            max(
                resources.getDimensionPixelSize(R.dimen.popup_view_drop_target_min_height),
                (width / aspectRatio).roundToInt(),
            )
        val left = ((containerWidth - width) / 2f).roundToInt()
        val top =
            max(
                topMarginPx,
                ((containerHeight * GUIDE_VERTICAL_POSITION_RATIO) - (height / 2f)).roundToInt(),
            )
        guideRect.set(left, top, left + width, top + height)
        updateLayoutParams<FrameLayout.LayoutParams> {
            this.width = guideRect.width()
            this.height = guideRect.height()
            gravity = Gravity.TOP or Gravity.START
            leftMargin = guideRect.left
            topMargin = guideRect.top
        }
        backgroundDrawable.cornerRadius =
            resources.getDimension(R.dimen.popup_view_drop_target_corner_radius)
        val strokeColor =
            context.getColor(
                if (armed) R.color.materialColorPrimary else R.color.materialColorOutline,
            )
        backgroundDrawable.setStroke(
            resources.getDimensionPixelSize(R.dimen.popup_view_drop_target_stroke_width),
            strokeColor,
        )
        backgroundDrawable.setColor(
            context.getColor(
                if (armed) {
                    R.color.materialColorPrimaryContainer
                } else {
                    R.color.materialColorSurfaceContainerHigh
                },
            ),
        )
        val contentColor =
            context.getColor(
                if (armed) {
                    R.color.materialColorOnPrimaryContainer
                } else {
                    R.color.materialColorOnSurface
                },
            )
        label.setText(
            if (armed) {
                R.string.popup_view_drag_target_release_label
            } else {
                R.string.popup_view_drag_target_label
            },
        )
        label.setTextColor(contentColor)
        icon.imageTintList = resources.getColorStateList(
            if (armed) {
                R.color.materialColorOnPrimaryContainer
            } else {
                R.color.materialColorOnSurface
            },
            context.theme,
        )
        alpha = MIN_ALPHA + ((1f - MIN_ALPHA) * boundedProgress)
        scaleX = if (armed) ARMED_SCALE else 1f + ((1f - boundedProgress) * 0.04f)
        scaleY = scaleX
    }

    fun animateLaunchCommit(endAction: Runnable) {
        animate().cancel()
        animate()
            .alpha(0f)
            .scaleX(COMMIT_SCALE)
            .scaleY(COMMIT_SCALE)
            .setDuration(COMMIT_DURATION)
            .setListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        visibility = View.GONE
                        animate().setListener(null)
                        endAction.run()
                    }
                },
            )
            .start()
    }

    fun getHitRectInParent(outRect: Rect) {
        outRect.set(guideRect)
    }

    companion object {
        private const val MIN_GUIDE_ASPECT_RATIO = 0.72f
        private const val MAX_GUIDE_ASPECT_RATIO = 1.45f
        private const val DEFAULT_TASK_ASPECT_RATIO = MIN_GUIDE_ASPECT_RATIO
        private const val GUIDE_VERTICAL_POSITION_RATIO = 0.33f
        private const val MIN_GUIDE_WIDTH_RATIO = 0.52f
        private const val MAX_GUIDE_WIDTH_RATIO = 0.68f
        private const val MIN_ALPHA = 0.22f
        private const val ARMED_SCALE = 0.98f
        private const val COMMIT_SCALE = 1.03f
        private const val HIDE_DURATION = 140L
        private const val COMMIT_DURATION = 140L
    }
}
