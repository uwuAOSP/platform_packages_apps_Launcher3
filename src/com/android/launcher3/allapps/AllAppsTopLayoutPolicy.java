/*
 * SPDX-FileCopyrightText: The uwuAOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */
package com.android.launcher3.allapps;

/**
 * Static top-inset calculations for All Apps, in pixels.
 *
 * <p>Container padding is measured from the container's origin. Search margins are measured from
 * its padded content origin, so a top inset owned by the container must not be added there again.
 * This policy does not calculate header scrolling, clipping, or animation translations.
 */
public final class AllAppsTopLayoutPolicy {

    private AllAppsTopLayoutPolicy() {}

    /** Keeps profile alignment while accounting for insets newer than the phone's profile. */
    public static int calculateTopPadding(int profilePadding, int insetTop, int phoneSpacing,
            boolean isPhone) {
        return isPhone ? Math.max(profilePadding, insetTop + phoneSpacing) : profilePadding;
    }

    /**
     * Returns the built-in, non-floating search field's margin in the parent's content coordinates.
     * Other hosts retain their legacy search inset until their own window geometry is validated.
     */
    public static int calculateSearchTopMargin(int insetTop, boolean containerOwnsTopInset,
            int phoneSearchMargin) {
        // The handle is drawn in a sibling behind search, not in the search layout's flow.
        return containerOwnsTopInset ? phoneSearchMargin : insetTop;
    }

    /** Places the visible search background one gap below the handle's bottom. */
    public static int calculatePhoneSearchMargin(int handleBottom, int gap, int overlap,
            int backgroundInset) {
        return handleBottom + gap - overlap - backgroundInset;
    }

    /**
     * Aligns the first icon drawable, not its cell boundary, below the visible search background.
     * Dynamic Header rows and tabs still add their existing heights on top of this base anchor.
     */
    public static int calculatePhoneHeaderMargin(int searchHeight, int gap, int overlap,
            int backgroundInset, int listTopPadding, int iconTopPadding) {
        return searchHeight + overlap - backgroundInset + gap - listTopPadding - iconTopPadding;
    }
}
