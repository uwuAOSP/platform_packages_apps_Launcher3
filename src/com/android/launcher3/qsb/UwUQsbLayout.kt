/*
 * Copyright (C) 2024 Lawnchair
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

package com.android.launcher3.qsb

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.android.launcher3.views.ActivityContext

/** Compose-backed search bar used by the Hotseat without replacing the AOSP OSE pipeline. */
class UwUQsbLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val activityContext = ActivityContext.lookupContextNoThrow(context)

    init {
        addView(
            ComposeView(context).apply {
                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
                setContent {
                    UwUQsbUi(
                        onClick = ::launchSearch,
                        onVoiceClick = ::launchVoiceSearch,
                    )
                }
            },
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT),
        )
    }

    private fun launchSearch() {
        launch(Intent(Intent.ACTION_WEB_SEARCH))
    }

    private fun launchVoiceSearch() {
        launch(Intent(Intent.ACTION_VOICE_SEARCH_HANDS_FREE))
    }

    private fun launch(intent: Intent) {
        val activityContext = activityContext ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
        activityContext.startActivitySafely(this, intent, null)
    }
}
