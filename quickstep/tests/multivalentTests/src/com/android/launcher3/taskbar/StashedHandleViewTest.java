/*
 * Copyright (C) 2026 The uwuAOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */
package com.android.launcher3.taskbar;

import static org.junit.Assert.assertEquals;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.android.launcher3.util.SandboxApplication;
import com.android.launcher3.util.TestActivityContext;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@SmallTest
@RunWith(AndroidJUnit4.class)
public class StashedHandleViewTest {
    @Rule public SandboxApplication app = new SandboxApplication();
    @Rule public TestActivityContext context = new TestActivityContext(app);

    @Test
    public void hideAndRestorePreservesGeometryAndCurrentColor() {
        StashedHandleView handle = new StashedHandleView(context);
        handle.layout(0, 0, 100, 10);
        handle.setBackgroundColor(Color.WHITE);
        Bitmap bitmap = Bitmap.createBitmap(100, 10, Bitmap.Config.ARGB_8888);
        try {
            Canvas canvas = new Canvas(bitmap);
            handle.draw(canvas);
            assertEquals(Color.WHITE, bitmap.getPixel(50, 5));

            handle.setHandleHidden(true);
            bitmap.eraseColor(Color.TRANSPARENT);
            handle.draw(canvas);
            assertEquals(Color.TRANSPARENT, bitmap.getPixel(50, 5));
            assertEquals(View.VISIBLE, handle.getVisibility());
            assertEquals(100, handle.getWidth());
            assertEquals(10, handle.getHeight());

            handle.setBackgroundColor(Color.GREEN);
            handle.setHandleHidden(false);
            handle.draw(canvas);
            assertEquals(Color.GREEN, bitmap.getPixel(50, 5));
        } finally {
            bitmap.recycle();
        }
    }
}
