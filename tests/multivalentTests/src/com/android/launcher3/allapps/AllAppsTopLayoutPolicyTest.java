/*
 * SPDX-FileCopyrightText: The uwuAOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */
package com.android.launcher3.allapps;

import static androidx.test.core.app.ApplicationProvider.getApplicationContext;

import static com.google.common.truth.Truth.assertThat;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Test;
import org.junit.runner.RunWith;

/** Tests static inset calculations and the RelativeLayout coordinate contract they rely on. */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class AllAppsTopLayoutPolicyTest {

    @Test
    public void phonePadding_respectsCurrentInsetAndLargerProfileAlignment() {
        assertThat(AllAppsTopLayoutPolicy.calculateTopPadding(0, 120, 8, true)).isEqualTo(128);
        assertThat(AllAppsTopLayoutPolicy.calculateTopPadding(200, 120, 8, true)).isEqualTo(200);
    }

    @Test
    public void otherHosts_keepLegacyMarginAndProfilePadding() {
        assertThat(AllAppsTopLayoutPolicy.calculateSearchTopMargin(120, false, 36)).isEqualTo(120);
        assertThat(AllAppsTopLayoutPolicy.calculateTopPadding(400, 120, 8, false))
                .isEqualTo(400);
        assertThat(AllAppsTopLayoutPolicy.calculateTopPadding(0, 120, 8, false)).isEqualTo(0);
    }

    @Test
    public void phoneSearchToHandleSpacing_doesNotDependOnInsets() {
        RelativeLayout parent = new RelativeLayout(getApplicationContext());
        View handle = new View(parent.getContext());
        RelativeLayout.LayoutParams handleParams = new RelativeLayout.LayoutParams(40, 4);
        handleParams.topMargin = 16;
        parent.addView(handle, handleParams);
        View search = new View(parent.getContext());
        search.setId(View.generateViewId());
        parent.addView(search, new RelativeLayout.LayoutParams(200, 64));
        View header = new View(parent.getContext());
        RelativeLayout.LayoutParams headerParams = new RelativeLayout.LayoutParams(200, 36);
        headerParams.addRule(RelativeLayout.ALIGN_TOP, search.getId());
        headerParams.topMargin = 33;
        parent.addView(header, headerParams);

        for (int inset : new int[]{0, 120, 60, 120, 0}) {
            applyInsetsAndLayout(parent, search, inset, true);
            assertThat(search.getTop()).isEqualTo(inset + 8 + 36);
            assertThat(search.getTop() - handle.getTop()).isEqualTo(36 - 16);
            assertThat(search.getTop()).isAtLeast(handle.getBottom());
            assertThat(header.getTop() - search.getTop()).isEqualTo(33);
        }
    }

    @Test
    public void searchMargin_usesHostSpacingRatherThanStatusBarHeight() {
        assertThat(AllAppsTopLayoutPolicy.calculateSearchTopMargin(120, true, 36)).isEqualTo(36);
        assertThat(AllAppsTopLayoutPolicy.calculateSearchTopMargin(60, true, 48)).isEqualTo(48);
        assertThat(AllAppsTopLayoutPolicy.calculateSearchTopMargin(0, true, 36)).isEqualTo(36);
    }

    @Test
    public void visibleGaps_matchAcrossSearchAndIconSizes() {
        int gap = 24;
        int handleBottom = 20;
        int overlap = 8;
        int backgroundInset = 6;
        int listPadding = 30;
        int searchMargin = AllAppsTopLayoutPolicy.calculatePhoneSearchMargin(
                handleBottom, gap, overlap, backgroundInset);
        int visibleSearchTop = searchMargin + overlap + backgroundInset;
        assertThat(visibleSearchTop - handleBottom).isEqualTo(gap);
        for (int searchHeight : new int[]{64, 72}) {
            for (int iconPadding : new int[]{8, 16, 24}) {
                int headerMargin = AllAppsTopLayoutPolicy.calculatePhoneHeaderMargin(
                        searchHeight, gap, overlap, backgroundInset, listPadding, iconPadding);
                int iconTop = searchMargin + headerMargin + listPadding + iconPadding;
                int visibleSearchBottom = searchMargin + overlap + searchHeight - backgroundInset;
                assertThat(iconTop - visibleSearchBottom).isEqualTo(gap);
            }
        }
    }

    @Test
    public void relativeLayout_legacyMarginIsAdditionalToContainerPadding() {
        RelativeLayout parent = new RelativeLayout(getApplicationContext());
        View search = new View(parent.getContext());
        parent.addView(search, new RelativeLayout.LayoutParams(200, 64));

        applyInsetsAndLayout(parent, search, 120, false);
        assertThat(search.getTop()).isEqualTo(128 + 120);
        applyInsetsAndLayout(parent, search, 120, true);
        assertThat(search.getTop()).isEqualTo(128 + 36);
    }

    private static void applyInsetsAndLayout(RelativeLayout parent, View search, int inset,
            boolean containerOwnsTopInset) {
        parent.setPadding(0, AllAppsTopLayoutPolicy.calculateTopPadding(0, inset, 8, true), 0, 0);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) search.getLayoutParams();
        lp.topMargin = AllAppsTopLayoutPolicy.calculateSearchTopMargin(
                inset, containerOwnsTopInset, 36);
        search.setLayoutParams(lp);
        parent.measure(View.MeasureSpec.makeMeasureSpec(500, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(1000, View.MeasureSpec.EXACTLY));
        parent.layout(0, 0, 500, 1000);
    }
}
