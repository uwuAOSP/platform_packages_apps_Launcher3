/*
 * Copyright (C) 2026 Lawnchair
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3.hotseat

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import com.android.launcher3.CellLayout
import com.android.launcher3.DeviceProfile
import com.android.launcher3.PagedView
import com.android.launcher3.R
import com.android.launcher3.Workspace
import com.android.launcher3.pageindicators.PageIndicatorDots

/** Horizontally pageable container for dock [CellLayout] pages. */
class HotseatPagedView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : PagedView<PageIndicatorDots>(context, attrs, defStyle) {

    fun interface OnDockPageChangeListener {
        fun onDockPageChanged(page: Int)
    }

    var isPagingEnabled: Boolean = false
        private set(value) {
            field = value
            applyPageIndicatorVisibility()
        }

    private var onDockPageChangeListener: OnDockPageChangeListener? = null

    init {
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
    }

    fun setPageIndicator(indicator: PageIndicatorDots?) {
        mPageIndicator = indicator
        indicator?.let {
            it.setMarkersCount(childCount)
            it.setActiveMarker(nextPage)
        }
        applyPageIndicatorVisibility()
    }

    private fun applyPageIndicatorVisibility() {
        mPageIndicator?.let {
            if (isPagingEnabled) {
                it.visibility = VISIBLE
                it.setShouldAutoHide(true)
            } else {
                it.setShouldAutoHide(false)
                it.visibility = GONE
            }
        }
    }

    override fun getPageAt(index: Int): CellLayout? = getChildAt(index) as? CellLayout

    fun currentCellLayout(): CellLayout? = getPageAt(nextPage)

    fun resetPages(hasVerticalHotseat: Boolean, workspace: Workspace<*>?, dp: DeviceProfile) {
        removeAllViews()
        val pageCount = if (hasVerticalHotseat) 1 else maxOf(1, dp.numHotseatPages)
        isPagingEnabled = pageCount > 1
        val inflater = LayoutInflater.from(context)
        repeat(pageCount) { pageIndex ->
            val page = inflater.inflate(R.layout.hotseat_page, this, false) as CellLayout
            page.setHotseatPageIndex(pageIndex)
            workspace?.let(page::setCellLayoutContainer)
            page.resetCellSize(dp)
            page.isLongClickable = false
            page.isHapticFeedbackEnabled = false
            if (hasVerticalHotseat) {
                page.setGridSize(1, dp.hotseatProfile.numShownIcons)
            } else {
                page.setGridSize(dp.hotseatProfile.numShownIcons, dp.numHotseatRows)
            }
            addView(
                page,
                LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT),
            )
        }
        mPageIndicator?.let {
            it.setMarkersCount(pageCount)
            it.setActiveMarker(0)
        }
        applyPageIndicatorVisibility()
        setCurrentPage(0)
        requestLayout()
    }

    fun setOnDockPageChangeListener(listener: OnDockPageChangeListener?) {
        onDockPageChangeListener = listener
    }

    override fun onPageEndTransition() {
        super.onPageEndTransition()
        onDockPageChangeListener?.onDockPageChanged(nextPage)
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        if (mMaxScroll > 0) mPageIndicator?.setScroll(l, mMaxScroll)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean =
        isPagingEnabled && super.onInterceptTouchEvent(ev)

    override fun onTouchEvent(ev: MotionEvent): Boolean =
        isPagingEnabled && super.onTouchEvent(ev)

    /** Returns the page under [x], expressed in this view's local coordinates. */
    fun findPageAtLocalX(x: Float): CellLayout? {
        val pageWidth = measuredWidth
        if (pageWidth <= 0 || childCount == 0) return currentCellLayout()
        var page = (scrollX + x).toInt() / pageWidth
        if (mIsRtl) page = childCount - 1 - page
        return getPageAt(page.coerceIn(0, childCount - 1))
    }
}
