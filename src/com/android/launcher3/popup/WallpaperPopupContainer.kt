/*
 * SPDX-FileCopyrightText: The uwuAOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.launcher3.popup

import android.content.Context
import android.view.View
import com.android.launcher3.model.data.ItemInfo
import com.android.launcher3.wallpaper.WallpaperCarouselView
import com.android.launcher3.views.ActivityContext

internal class WallpaperPopupContainer(
    context: Context,
    originalView: View,
    itemInfo: ItemInfo,
) : PopupContainer<ActivityContext>(context, originalView, itemInfo, true) {

    override fun addSystemShortcutHeader(activityContext: ActivityContext) {
        val carousel = WallpaperCarouselView(getContext())
        carousel.layoutParams = LayoutParams(dp(280), dp(80))
        addView(carousel, 0)
        carousel.load { close(true) }
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    companion object {
        fun create(
            context: Context,
            originalView: View,
            itemInfo: ItemInfo,
        ): WallpaperPopupContainer {
            return WallpaperPopupContainer(context, originalView, itemInfo).apply {
                id = com.android.launcher3.R.id.popup_container
                clipChildren = false
                clipToPadding = false
                layoutParams = LayoutParams(
                    LayoutParams.WRAP_CONTENT,
                    LayoutParams.WRAP_CONTENT,
                )
            }
        }
    }
}
