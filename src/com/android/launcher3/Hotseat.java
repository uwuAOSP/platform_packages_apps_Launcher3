/*
 * Copyright (C) 2011 The Android Open Source Project
 * Copyright (C) 2026 Lawnchair
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.android.launcher3;

import static android.view.View.MeasureSpec.makeMeasureSpec;
import static com.android.launcher3.LauncherAnimUtils.VIEW_TRANSLATE_X;
import static com.android.launcher3.util.MultiTranslateDelegate.INDEX_BUBBLE_ADJUSTMENT_ANIM;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

import com.android.launcher3.accessibility.DragAndDropAccessibilityDelegate;
import com.android.launcher3.celllayout.CellLayoutLayoutParams;
import com.android.launcher3.dagger.LauncherComponentProvider;
import com.android.launcher3.dragndrop.SystemDragItemInfo;
import com.android.launcher3.homescreenfiles.HomeScreenFilesUtilsKt;
import com.android.launcher3.hotseat.HotseatPagedView;
import com.android.launcher3.model.data.ItemInfo;
import com.android.launcher3.model.data.LauncherAppWidgetInfo;
import com.android.launcher3.pageindicators.PageIndicatorDots;
import com.android.launcher3.util.HorizontalInsettableView;
import com.android.launcher3.util.LauncherBindableItemsContainer.ItemOperator;
import com.android.launcher3.util.MultiPropertyFactory;
import com.android.launcher3.util.MultiPropertyFactory.MultiProperty;
import com.android.launcher3.util.MultiTranslateDelegate;
import com.android.launcher3.util.MultiValueAlpha;
import com.android.launcher3.util.Themes;
import com.android.launcher3.views.ActivityContext;
import com.android.launcher3.widget.PendingAddWidgetInfo;

import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/** Bottom dock containing pageable icon grids and an optional search bar. */
public class Hotseat extends FrameLayout implements Insettable {

    public static final int ALPHA_CHANNEL_TASKBAR_ALIGNMENT = 0;
    public static final int ALPHA_CHANNEL_PREVIEW_RENDERER = 1;
    public static final int ALPHA_CHANNEL_TASKBAR_STASH = 2;
    public static final int ALPHA_CHANNEL_ASSISTANT_VISIBILITY = 3;
    public static final int ALPHA_CHANNEL_CHANNELS_COUNT = 4;

    @Retention(RetentionPolicy.RUNTIME)
    @IntDef({ALPHA_CHANNEL_TASKBAR_ALIGNMENT, ALPHA_CHANNEL_PREVIEW_RENDERER,
            ALPHA_CHANNEL_TASKBAR_STASH, ALPHA_CHANNEL_ASSISTANT_VISIBILITY})
    public @interface HotseatQsbAlphaId { }

    public static final int ICONS_TRANSLATION_X_NAV_BAR_ALIGNMENT = 0;
    public static final int ICONS_TRANSLATION_X_CHANNELS_COUNT = 1;

    @Retention(RetentionPolicy.RUNTIME)
    @IntDef({ICONS_TRANSLATION_X_NAV_BAR_ALIGNMENT})
    public @interface IconsTranslationX { }

    public static final float QSB_CENTER_FACTOR = .325f;
    private static final int BUBBLE_BAR_ADJUSTMENT_ANIMATION_DURATION_MS = 250;
    private static final int DOCK_PAGE_INDICATOR_HEIGHT_DP = 8;

    @ViewDebug.ExportedProperty(category = "launcher")
    private boolean mHasVerticalHotseat;
    private Workspace<?> mWorkspace;
    private boolean mSendTouchToWorkspace;
    private boolean mSendTouchToPager;
    private final MultiValueAlpha mIconsAlphaChannels;
    private final MultiValueAlpha mQsbAlphaChannels;
    private final MultiPropertyFactory mIconsTranslationXFactory;
    private @Nullable MultiProperty mQsbTranslationX;

    private final ActivityContext mActivity;
    private final LauncherPrefs mPrefs;
    private final View mQsb;
    private final FrameLayout mIconsContainer;
    private final HotseatPagedView mPagedView;
    private final PageIndicatorDots mPageIndicator;
    private final int mPageIndicatorHeight;

    public Hotseat(Context context) {
        this(context, null);
    }

    public Hotseat(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Hotseat(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mActivity = ActivityContext.lookupContext(context);
        mPrefs = LauncherPrefs.get(context);

        mIconsContainer = new FrameLayout(context);
        mIconsContainer.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mIconsContainer.setClipChildren(false);
        mIconsContainer.setClipToPadding(false);
        addView(mIconsContainer);

        mPagedView = new HotseatPagedView(context);
        mPagedView.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mIconsContainer.addView(mPagedView);

        float indicatorFactor = mPrefs.get(LauncherPrefs.HOTSEAT_PAGE_INDICATOR_HEIGHT_FACTOR);
        mPageIndicatorHeight = Math.round(DOCK_PAGE_INDICATOR_HEIGHT_DP
                * getResources().getDisplayMetrics().density * indicatorFactor);
        mPageIndicator = new PageIndicatorDots(context);
        LayoutParams indicatorLp = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, mPageIndicatorHeight);
        indicatorLp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        mPageIndicator.setLayoutParams(indicatorLp);
        mPageIndicator.setVisibility(GONE);
        mIconsContainer.addView(mPageIndicator);
        mPagedView.setPageIndicator(mPageIndicator);

        mQsb = LauncherComponentProvider.get(context).getQsbWidgetFactory().createView(this);
        mQsb.setVisibility(mPrefs.get(LauncherPrefs.HOTSEAT_ENABLED)
                && !"disabled".equals(mPrefs.get(LauncherPrefs.HOTSEAT_MODE))
                ? VISIBLE : GONE);
        addView(mQsb);

        mPagedView.resetPages(false, null, mActivity.getDeviceProfile());
        mIconsAlphaChannels = new MultiValueAlpha(mIconsContainer, ALPHA_CHANNEL_CHANNELS_COUNT);
        mIconsAlphaChannels.setUpdateVisibility(true);
        if (mQsb instanceof Reorderable qsbReorderable) {
            mQsbTranslationX = qsbReorderable.getTranslateDelegate()
                    .getTranslationX(MultiTranslateDelegate.INDEX_NAV_BAR_ANIM);
        }
        mIconsTranslationXFactory = new MultiPropertyFactory<>(mIconsContainer,
                VIEW_TRANSLATE_X, ICONS_TRANSLATION_X_CHANNELS_COUNT, Float::sum);
        mQsbAlphaChannels = new MultiValueAlpha(mQsb, ALPHA_CHANNEL_CHANNELS_COUNT);
        mQsbAlphaChannels.setUpdateVisibility(true);

        setUpBackground(0);
        setClipChildren(false);
        setClipToPadding(false);
    }

    private void setUpBackground(int extraTopInset) {
        if (!mPrefs.get(LauncherPrefs.HOTSEAT_ENABLED)
                || !mPrefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_ENABLED)) {
            setBackground(null);
            return;
        }
        int configuredColor = mPrefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_COLOR);
        int baseColor = configuredColor == 0
                ? Themes.getColorBackgroundFloating(getContext()) : configuredColor;
        int alpha = Math.round(mPrefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_ALPHA) * 2.55f);
        int finalColor = Color.argb(alpha, Color.red(baseColor), Color.green(baseColor),
                Color.blue(baseColor));
        float cornerRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mPrefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_CORNER_RADIUS),
                getResources().getDisplayMetrics());
        GradientDrawable background = new GradientDrawable();
        background.setColor(finalColor);
        background.setCornerRadius(cornerRadius);
        setBackground(new InsetDrawable(background,
                mPrefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_INSET_LEFT),
                mPrefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_INSET_TOP) + extraTopInset,
                mPrefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_INSET_RIGHT),
                mPrefs.get(LauncherPrefs.HOTSEAT_BACKGROUND_INSET_BOTTOM)));
    }

    public MultiProperty getIconsTranslationX(@IconsTranslationX int channelId) {
        return mIconsTranslationXFactory.get(channelId);
    }

    @Nullable
    public MultiProperty getQsbTranslationX() {
        return mQsbTranslationX;
    }

    public HotseatPagedView getPagedView() {
        return mPagedView;
    }

    @Nullable
    public CellLayout getPageAt(int page) {
        return mPagedView.getPageAt(page);
    }

    @Nullable
    public CellLayout getCurrentPageLayout() {
        return mPagedView.currentCellLayout();
    }

    public CellLayout[] getPageLayouts() {
        int count = mPagedView.getPageCount();
        CellLayout[] pages = new CellLayout[count];
        for (int i = 0; i < count; i++) pages[i] = mPagedView.getPageAt(i);
        return pages;
    }

    public boolean isHotseatPage(View layout) {
        return layout instanceof CellLayout && layout.getParent() == mPagedView;
    }

    public int getCellXFromOrder(int rank) {
        if (mHasVerticalHotseat) return 0;
        DeviceProfile dp = mActivity.getDeviceProfile();
        return getLocalRank(rank, dp) % dp.getHotseatProfile().getNumShownIcons();
    }

    public int getCellYFromOrder(int rank) {
        if (mHasVerticalHotseat) {
            CellLayout page = getCurrentPageLayout();
            int countY = page == null ? mActivity.getDeviceProfile().getHotseatProfile()
                    .getNumShownIcons() : page.getCountY();
            return countY - (rank + 1);
        }
        DeviceProfile dp = mActivity.getDeviceProfile();
        return getLocalRank(rank, dp) / dp.getHotseatProfile().getNumShownIcons();
    }

    public int getPageFromOrder(int rank) {
        if (mHasVerticalHotseat) return 0;
        DeviceProfile dp = mActivity.getDeviceProfile();
        return rank / Math.max(1,
                dp.getHotseatProfile().getNumShownIcons() * dp.numHotseatRows);
    }

    private static int getLocalRank(int rank, DeviceProfile dp) {
        return rank % Math.max(1,
                dp.getHotseatProfile().getNumShownIcons() * dp.numHotseatRows);
    }

    boolean isHasVerticalHotseat() {
        return mHasVerticalHotseat;
    }

    public boolean isValidDropTarget(DropTarget.DragObject dragObject) {
        ShortcutAndWidgetContainer container = getShortcutsAndWidgets();
        return container != null && container.getVisibility() == VISIBLE && isSupportedDrag(dragObject);
    }

    public void resetLayout(boolean hasVerticalHotseat) {
        ActivityContext activityContext = ActivityContext.lookupContext(getContext());
        boolean bubbleBarEnabled = activityContext.isBubbleBarEnabled();
        boolean hasBubbles = activityContext.hasBubbles();
        mHasVerticalHotseat = hasVerticalHotseat;
        DeviceProfile dp = mActivity.getDeviceProfile();
        mPagedView.resetPages(hasVerticalHotseat, mWorkspace, dp);
        if (bubbleBarEnabled) {
            for (CellLayout page : getPageLayouts()) {
                if (dp.shouldAdjustHotseatForBubbleBar(getContext(), hasBubbles)) {
                    page.getShortcutsAndWidgets().setTranslationProvider(
                            cellX -> dp.getHotseatAdjustedTranslation(getContext(), cellX));
                } else {
                    page.getShortcutsAndWidgets().setTranslationProvider(null);
                }
            }
        }
    }

    public void adjustForBubbleBar(boolean isBubbleBarVisible) {
        DeviceProfile dp = mActivity.getDeviceProfile();
        boolean shouldAdjust = isBubbleBarVisible
                && dp.shouldAdjustHotseatOrQsbForBubbleBar(getContext());
        boolean shouldAdjustHotseat = shouldAdjust && dp.shouldAlignBubbleBarWithHotseat();
        AnimatorSet animatorSet = new AnimatorSet();
        for (CellLayout page : getPageLayouts()) {
            ShortcutAndWidgetContainer icons = page.getShortcutsAndWidgets();
            icons.setTranslationProvider(shouldAdjustHotseat
                    ? cellX -> dp.getHotseatAdjustedTranslation(getContext(), cellX) : null);
            for (int i = 0; i < icons.getChildCount(); i++) {
                View child = icons.getChildAt(i);
                if (child.getLayoutParams() instanceof CellLayoutLayoutParams lp) {
                    float tx = shouldAdjustHotseat
                            ? dp.getHotseatAdjustedTranslation(getContext(), lp.getCellX()) : 0;
                    if (child instanceof Reorderable reorderable) {
                        animatorSet.play(reorderable.getTranslateDelegate()
                                .getTranslationX(INDEX_BUBBLE_ADJUSTMENT_ANIM).animateToValue(tx));
                    } else {
                        animatorSet.play(ObjectAnimator.ofFloat(child, VIEW_TRANSLATE_X, tx));
                    }
                }
            }
        }
        boolean shouldAdjustQsb = shouldAdjustHotseat
                || (shouldAdjust && dp.shouldAlignBubbleBarWithQSB());
        if (mQsb instanceof HorizontalInsettableView insettableQsb) {
            float target = shouldAdjustQsb
                    ? (float) dp.getWorkspaceProfile().getIconSizePx()
                    / dp.getHotseatProfile().getQsbWidth() : 0;
            ValueAnimator animator = ValueAnimator.ofFloat(
                    insettableQsb.getHorizontalInsets(), target);
            animator.addUpdateListener(a -> insettableQsb.setHorizontalInsets(
                    (float) a.getAnimatedValue()));
            animatorSet.play(animator);
        }
        animatorSet.setDuration(BUBBLE_BAR_ADJUSTMENT_ANIMATION_DURATION_MS).start();
    }

    @Override
    public void setInsets(Rect insets) {
        LayoutParams lp = (LayoutParams) getLayoutParams();
        DeviceProfile dp = mActivity.getDeviceProfile();
        int topOverlap = 0;
        if (dp.isVerticalBarLayout()) {
            mQsb.setVisibility(GONE);
            mPageIndicator.setVisibility(GONE);
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.gravity = dp.isSeascape() ? Gravity.LEFT : Gravity.RIGHT;
            lp.width = dp.getHotseatProfile().getBarSizePx()
                    + (dp.isSeascape() ? insets.left : insets.right);
        } else {
            mQsb.setVisibility(mPrefs.get(LauncherPrefs.HOTSEAT_ENABLED)
                    && !"disabled".equals(mPrefs.get(LauncherPrefs.HOTSEAT_MODE))
                    ? VISIBLE : GONE);
            lp.gravity = Gravity.BOTTOM;
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            int totalHeightForQsb = dp.getQsbOffsetY()
                    + dp.getHotseatProfile().getQsbHeight();
            topOverlap = Math.max(0,
                    totalHeightForQsb - dp.getHotseatProfile().getBarSizePx());
            lp.height = dp.getHotseatProfile().getBarSizePx() + topOverlap;
        }
        Rect padding = dp.getHotseatLayoutPadding(getContext());
        setPadding(padding.left, padding.top + topOverlap, padding.right, padding.bottom);
        mIconsContainer.setVisibility(mPrefs.get(LauncherPrefs.HOTSEAT_ENABLED)
                ? VISIBLE : GONE);
        mPageIndicator.setVisibility(mPrefs.get(LauncherPrefs.HOTSEAT_ENABLED)
                ? mPageIndicator.getVisibility() : GONE);
        setUpBackground(topOverlap);
        setLayoutParams(lp);
        setVisibility(mPrefs.get(LauncherPrefs.HOTSEAT_ENABLED)
                || !"disabled".equals(mPrefs.get(LauncherPrefs.HOTSEAT_MODE))
                ? VISIBLE : GONE);
        InsettableFrameLayout.dispatchInsets(this, insets);
    }

    public void setWorkspace(Workspace<?> workspace) {
        mWorkspace = workspace;
        for (CellLayout page : getPageLayouts()) page.setCellLayoutContainer(workspace);
    }

    private boolean isTouchOnQsb(MotionEvent event) {
        return mQsb.getVisibility() == VISIBLE
                && event.getX() >= mQsb.getLeft() && event.getX() < mQsb.getRight()
                && event.getY() >= mQsb.getTop() && event.getY() < mQsb.getBottom();
    }

    private MotionEvent obtainPagerEvent(MotionEvent event) {
        MotionEvent pagerEvent = MotionEvent.obtain(event);
        pagerEvent.offsetLocation(-mIconsContainer.getLeft() - mPagedView.getLeft(),
                -mIconsContainer.getTop() - mPagedView.getTop());
        return pagerEvent;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        int yThreshold = getMeasuredHeight() - getPaddingBottom();
        if (event.getY() > yThreshold || isTouchOnQsb(event)) return false;
        if (mPagedView.isPagingEnabled()) {
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
                mSendTouchToPager = true;
                mSendTouchToWorkspace = false;
            }
            return mSendTouchToPager;
        }
        if (mWorkspace != null) {
            mSendTouchToWorkspace = mWorkspace.onInterceptTouchEvent(event);
            return mSendTouchToWorkspace;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mSendTouchToPager) {
            MotionEvent pagerEvent = obtainPagerEvent(event);
            boolean handled = mPagedView.dispatchTouchEvent(pagerEvent);
            pagerEvent.recycle();
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                mSendTouchToPager = false;
            }
            return handled;
        }
        if (mSendTouchToWorkspace) {
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                mSendTouchToWorkspace = false;
            }
            return mWorkspace.onTouchEvent(event);
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        int pagerWidth = Math.max(0, width - getPaddingLeft() - getPaddingRight());
        int pagerHeight = Math.max(0, height - getPaddingTop() - getPaddingBottom());
        boolean showIndicator = mPagedView.isPagingEnabled()
                && !mActivity.getDeviceProfile().isVerticalBarLayout();
        int indicatorSpace = showIndicator ? mPageIndicatorHeight : 0;
        mIconsContainer.measure(makeMeasureSpec(pagerWidth, MeasureSpec.EXACTLY),
                makeMeasureSpec(pagerHeight, MeasureSpec.EXACTLY));
        mPagedView.measure(makeMeasureSpec(pagerWidth, MeasureSpec.EXACTLY),
                makeMeasureSpec(Math.max(0, pagerHeight - indicatorSpace), MeasureSpec.EXACTLY));
        if (showIndicator) {
            mPageIndicator.measure(makeMeasureSpec(pagerWidth, MeasureSpec.EXACTLY),
                    makeMeasureSpec(mPageIndicatorHeight, MeasureSpec.EXACTLY));
        }
        DeviceProfile dp = mActivity.getDeviceProfile();
        int qsbWidth = dp.getHotseatProfile().isQsbInline()
                ? dp.getHotseatProfile().getQsbWidth() : pagerWidth;
        mQsb.measure(makeMeasureSpec(Math.max(0, qsbWidth), MeasureSpec.EXACTLY),
                makeMeasureSpec(dp.getHotseatProfile().getQsbHeight(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int width = r - l;
        int height = b - t;
        mIconsContainer.layout(getPaddingLeft(), getPaddingTop(), width - getPaddingRight(),
                height - getPaddingBottom());
        int containerWidth = mIconsContainer.getWidth();
        int containerHeight = mIconsContainer.getHeight();
        DeviceProfile dp = mActivity.getDeviceProfile();
        boolean showIndicator = mPagedView.isPagingEnabled() && !dp.isVerticalBarLayout();
        int indicatorSpace = showIndicator ? mPageIndicatorHeight : 0;
        mPagedView.layout(0, 0, containerWidth, Math.max(0, containerHeight - indicatorSpace));
        if (showIndicator) {
            int indicatorTop = containerHeight - mPageIndicatorHeight;
            mPageIndicator.layout(0, indicatorTop, containerWidth,
                    indicatorTop + mPageIndicatorHeight);
            mPageIndicator.setVisibility(VISIBLE);
        } else {
            mPageIndicator.setVisibility(GONE);
        }
        int qsbWidth = mQsb.getMeasuredWidth();
        int left;
        if (dp.getHotseatProfile().isQsbInline()) {
            int qsbSpace = dp.getHotseatProfile().getBorderSpace();
            left = Utilities.isRtl(getResources()) ? r - getPaddingRight() + qsbSpace
                    : l + getPaddingLeft() - qsbWidth - qsbSpace;
        } else {
            left = (width - qsbWidth) / 2;
        }
        int bottom = height - dp.getQsbOffsetY();
        mQsb.layout(left, bottom - dp.getHotseatProfile().getQsbHeight(), left + qsbWidth, bottom);
    }

    public void setIconsAlpha(float alpha, @HotseatQsbAlphaId int channelId) {
        getIconsAlpha(channelId).setValue(alpha);
    }

    public void setQsbAlpha(float alpha, @HotseatQsbAlphaId int channelId) {
        getQsbAlpha(channelId).setValue(alpha);
    }

    public MultiProperty getIconsAlpha(@HotseatQsbAlphaId int channelId) {
        return mIconsAlphaChannels.get(channelId);
    }

    public MultiProperty getQsbAlpha(@HotseatQsbAlphaId int channelId) {
        return mQsbAlphaChannels.get(channelId);
    }

    public View getQsb() {
        return mQsb;
    }

    @Nullable
    public ShortcutAndWidgetContainer getShortcutsAndWidgets() {
        CellLayout page = getCurrentPageLayout();
        return page == null ? null : page.getShortcutsAndWidgets();
    }

    @Nullable
    public DragAndDropAccessibilityDelegate getDragAndDropAccessibilityDelegate() {
        CellLayout page = getCurrentPageLayout();
        return page == null ? null : page.getDragAndDropAccessibilityDelegate();
    }

    public int getSpringLoadedBarTopMarginPx() {
        return mActivity.getDeviceProfile().getHotseatProfile().getSpringLoadedBarTopMarginPx();
    }

    @Nullable
    public View getChildAt(int cellX, int cellY) {
        CellLayout page = getCurrentPageLayout();
        return page == null ? null : page.getChildAt(cellX, cellY);
    }

    @Nullable
    public View mapOverItems(ItemOperator operator) {
        for (CellLayout page : getPageLayouts()) {
            View match = page.mapOverItems(operator);
            if (match != null) return match;
        }
        return null;
    }

    public void dump(String prefix, PrintWriter writer) {
        writer.println(prefix + "Hotseat:");
        writer.println(prefix + "\tpages: " + mPagedView.getPageCount()
                + " pagingEnabled=" + mPagedView.isPagingEnabled());
        mIconsAlphaChannels.dump(prefix + "\t", writer, "mIconsAlphaChannels",
                "ALPHA_CHANNEL_TASKBAR_ALIGNMENT", "ALPHA_CHANNEL_PREVIEW_RENDERER",
                "ALPHA_CHANNEL_TASKBAR_STASH");
        mQsbAlphaChannels.dump(prefix + "\t", writer, "mQsbAlphaChannels",
                "ALPHA_CHANNEL_TASKBAR_ALIGNMENT", "ALPHA_CHANNEL_PREVIEW_RENDERER",
                "ALPHA_CHANNEL_TASKBAR_STASH");
    }

    private boolean isSupportedDrag(DropTarget.DragObject dragObject) {
        return !(HomeScreenFilesUtilsKt.isFileSystemItem(dragObject.dragInfo)
                || dragObject.dragInfo instanceof LauncherAppWidgetInfo
                || dragObject.dragInfo instanceof PendingAddWidgetInfo
                || dragObject.dragInfo instanceof SystemDragItemInfo);
    }
}
