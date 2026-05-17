/*
 * Copyright (C) 2026 The Android Open Source Project
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

package com.android.launcher3.qsb;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.android.launcher3.Launcher;

/**
 * Launches assistant-like entry points from the hotseat QSB.
 */
public class AiModeButtonView extends ImageView {

    private static final String ACTION_VOICE_ASSIST = "android.intent.action.VOICE_ASSIST";

    public AiModeButtonView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AiModeButtonView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setScaleType(ScaleType.CENTER);
        setOnClickListener(v -> launchAiEntry(context));
    }

    private void launchAiEntry(Context context) {
        String searchPackage = QsbLayout.getSearchPackage(context);
        Intent[] candidates = new Intent[] {
                new Intent("com.google.android.PIXEL_SEARCH")
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                new Intent(Intent.ACTION_ASSIST)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                new Intent(ACTION_VOICE_ASSIST)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
                new Intent(Intent.ACTION_WEB_SEARCH)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
        };
        for (Intent candidate : candidates) {
            if (searchPackage != null) {
                candidate.setPackage(searchPackage);
            }
            if (candidate.resolveActivity(context.getPackageManager()) == null) {
                continue;
            }
            try {
                context.startActivity(candidate);
                return;
            } catch (ActivityNotFoundException | SecurityException ignored) {
                // Try the next entry point.
            }
        }
        Launcher.getLauncher(context).startSearch("", false, null, true);
    }
}
