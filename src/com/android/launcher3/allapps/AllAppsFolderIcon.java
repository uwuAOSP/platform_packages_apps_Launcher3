/*
 * Copyright (C) 2024 Lawnchair
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package com.android.launcher3.allapps;

import static com.android.launcher3.logging.StatsLogManager.LauncherEvent.LAUNCHER_FOLDER_OPEN;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.android.launcher3.R;
import com.android.launcher3.folder.FolderIcon;
import com.android.launcher3.logging.StatsLogManager;
import com.android.launcher3.model.data.FolderInfo;
import com.android.launcher3.model.data.ItemInfoWithIcon;
import com.android.launcher3.views.ActivityContext;

/**
 * All Apps category preview: a 2x2 icon grid, where the last cell opens the folder.
 */
public class AllAppsFolderIcon extends FolderIcon {
    private static final int GRID_COLUMNS = 2;
    private static final int MAX_PREVIEW_APPS = 3;
    private static final int PREVIEW_VERTICAL_PADDING_DP = 16;

    public AllAppsFolderIcon(Context context) {
        this(context, null);
    }

    public AllAppsFolderIcon(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void bindPreviewGrid(ActivityContext activityContext, FolderInfo folderInfo) {
        setIconVisible(true);
        setClickable(false);

        GridLayout previewGrid = findViewById(R.id.all_apps_folder_preview_grid);
        previewGrid.removeAllViews();
        previewGrid.setColumnCount(GRID_COLUMNS);
        previewGrid.setRowCount(GRID_COLUMNS);

        int gridSize = getPreviewGridSize(activityContext);
        ViewGroup.LayoutParams previewLp = previewGrid.getLayoutParams();
        previewLp.width = gridSize;
        previewLp.height = gridSize;
        previewGrid.setLayoutParams(previewLp);

        int cellSize = gridSize / GRID_COLUMNS;
        int iconSize = getPreviewIconSize(activityContext, cellSize);
        int appCount = Math.min(MAX_PREVIEW_APPS, folderInfo.getContents().size());
        for (int i = 0; i < MAX_PREVIEW_APPS; i++) {
            ImageView icon = newPreviewCell(cellSize, iconSize);
            if (i < appCount && folderInfo.getContents().get(i) instanceof ItemInfoWithIcon info) {
                icon.setImageDrawable(info.newIcon(getContext()));
                icon.setContentDescription(info.contentDescription);
                icon.setTag(info);
                icon.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
                icon.setFocusable(true);
                icon.setClickable(true);
                icon.setOnClickListener(activityContext.getItemOnClickListener());
            } else {
                icon.setVisibility(INVISIBLE);
            }
            previewGrid.addView(icon);
        }

        ImageView more = newPreviewCell(cellSize, iconSize);
        more.setImageResource(R.drawable.ic_all_apps_folder_more);
        more.setContentDescription(getResources().getString(
                R.string.all_apps_folder_more_content_description, folderInfo.title));
        more.setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        more.setFocusable(true);
        more.setClickable(true);
        more.setOnClickListener(this::openFolder);
        previewGrid.addView(more);

        View folderName = findViewById(R.id.folder_icon_name);
        FrameLayout.LayoutParams nameLp = (FrameLayout.LayoutParams) folderName.getLayoutParams();
        nameLp.topMargin = gridSize + activityContext.getDeviceProfile()
                .getAllAppsProfile().getIconDrawablePaddingPx();
        nameLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        folderName.setLayoutParams(nameLp);
    }

    private void openFolder(View view) {
        if (!getFolder().isOpen() && !getFolder().isDestroyed()) {
            getFolder().animateOpen();
            StatsLogManager.newInstance(getContext()).logger().withItemInfo(getFolder().mInfo)
                    .log(LAUNCHER_FOLDER_OPEN);
        }
    }

    @Override
    public void setIconVisible(boolean visible) {
        super.setIconVisible(false);
        View previewGrid = findViewById(R.id.all_apps_folder_preview_grid);
        if (previewGrid != null) {
            previewGrid.setVisibility(visible ? VISIBLE : INVISIBLE);
        }
        View folderName = findViewById(R.id.folder_icon_name);
        if (folderName != null) {
            folderName.setVisibility(visible ? VISIBLE : INVISIBLE);
        }
    }

    private ImageView newPreviewCell(int cellSize, int iconSize) {
        ImageView icon = new ImageView(getContext());
        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
        lp.width = cellSize;
        lp.height = cellSize;
        icon.setLayoutParams(lp);
        int padding = Math.max(0, (cellSize - iconSize) / 2);
        icon.setPadding(padding, padding, padding, padding);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        return icon;
    }

    private int getPreviewGridSize(ActivityContext activityContext) {
        int iconSize = activityContext.getDeviceProfile().getAllAppsProfile().getIconSizePx();
        return Math.max(dpToPx(112), iconSize * 2);
    }

    public static int getExpectedHeight(ActivityContext activityContext) {
        Context context = activityContext.asContext();
        int iconSize = activityContext.getDeviceProfile().getAllAppsProfile().getIconSizePx();
        int gridSize = Math.max(dpToPx(context, 112), iconSize * 2);
        int labelHeight = activityContext.getDeviceProfile().getAllAppsProfile().getCellHeightPx()
                - iconSize;
        return gridSize + Math.max(labelHeight, dpToPx(context, 40))
                + dpToPx(context, PREVIEW_VERTICAL_PADDING_DP);
    }

    private int getPreviewIconSize(ActivityContext activityContext, int cellSize) {
        int iconSize = activityContext.getDeviceProfile().getAllAppsProfile().getIconSizePx();
        return Math.min(cellSize - dpToPx(4), Math.max(dpToPx(44), iconSize));
    }

    private int dpToPx(int dp) {
        return dpToPx(getContext(), dp);
    }

    private static int dpToPx(Context context, int dp) {
        return Math.round(dp * context.getResources().getDisplayMetrics().density);
    }
}
