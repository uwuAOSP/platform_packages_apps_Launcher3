/*
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
package com.android.launcher3.settings

import android.content.Context
import androidx.compose.runtime.RememberObserver

class LauncherPreviewManager(private val context: Context) : RememberObserver {

    private var activePreview: LauncherPreviewView? = null

    fun createPreviewView(idp: com.android.launcher3.InvariantDeviceProfile): LauncherPreviewView {
        destroyActivePreview()
        return LauncherPreviewView(context, idp).also { activePreview = it }
    }

    private fun destroyActivePreview() {
        activePreview?.destroy()
        activePreview = null
    }

    override fun onRemembered() = Unit

    override fun onForgotten() = destroyActivePreview()

    override fun onAbandoned() = destroyActivePreview()
}
