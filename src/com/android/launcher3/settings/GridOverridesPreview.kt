/*
 * Copyright (C) 2026 The uwuAOSP Project
 *
 * Portions of this file follow Lawnchair's GridOverridesPreview implementation
 * from the 16-dev branch.
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

import android.app.WallpaperManager
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.unit.dp
import com.android.launcher3.InvariantDeviceProfile
import com.android.launcher3.grid.GridSizeOverrides.GridSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun GridOverridesPreview(
    columns: Int,
    rows: Int,
    hotseatColumns: Int,
    hotseatColumnsUnfolded: Int = hotseatColumns,
    iconShapeKey: String? = null,
    modifier: Modifier = Modifier,
) {
    val previewIdp = createPreviewIdp(
        columns = columns,
        rows = rows,
        hotseatColumns = hotseatColumns,
        hotseatColumnsUnfolded = hotseatColumnsUnfolded,
    )
    val isPortrait = LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT
    val previewAspectRatio = remember(previewIdp, isPortrait) {
        val matchingProfile = previewIdp.supportedProfiles.firstOrNull { profile ->
            profile.deviceProperties.isLandscape != isPortrait
        } ?: previewIdp.supportedProfiles.firstOrNull { profile ->
            !profile.deviceProperties.deviceConfiguration.isMultiDisplay
        } ?: previewIdp.supportedProfiles.firstOrNull()

        val width = matchingProfile?.deviceProperties?.widthPx?.toFloat()
        val height = matchingProfile?.deviceProperties?.heightPx?.toFloat()
        if (width == null || height == null || height <= 0f) 1f else width / height
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        val fitByWidth = maxHeight == 0.dp || maxWidth == 0.dp
                || maxWidth / maxHeight <= previewAspectRatio
        val previewModifier = if (fitByWidth) {
            Modifier.requiredSize(maxWidth, maxWidth / previewAspectRatio)
        } else {
            Modifier.requiredSize(maxHeight * previewAspectRatio, maxHeight)
        }

        DummyLauncherBox(
            idp = previewIdp,
            iconShapeKey = iconShapeKey,
            modifier = previewModifier.clip(MaterialTheme.shapes.large),
        )
    }
}

@Composable
private fun DummyLauncherBox(
    idp: InvariantDeviceProfile,
    iconShapeKey: String?,
    modifier: Modifier,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surfaceContainerHigh),
        )
        WallpaperPreview(modifier = Modifier.fillMaxSize())
        DummyLauncherLayout(
            idp = idp,
            iconShapeKey = iconShapeKey,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun DummyLauncherLayout(
    idp: InvariantDeviceProfile,
    iconShapeKey: String?,
    modifier: Modifier,
) {
    val context = LocalContext.current
    val previewManager = remember { LauncherPreviewManager(context) }
    val previewView = remember(idp) { previewManager.createPreviewView(idp, iconShapeKey) }

    key(previewView) {
        AndroidView(
            factory = { previewView },
            update = { it.updateIconShape(iconShapeKey) },
            modifier = modifier,
        )
    }
}

@Composable
private fun WallpaperPreview(modifier: Modifier) {
    val context = LocalContext.current
    val wallpaper by produceState<Drawable?>(null, context) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                val manager = WallpaperManager.getInstance(context)
                manager.wallpaperInfo?.loadThumbnail(context.packageManager)
                    ?: manager.getWallpaperFile(WallpaperManager.FLAG_SYSTEM)?.use { descriptor ->
                        BitmapFactory.decodeFileDescriptor(descriptor.fileDescriptor)?.let { bitmap ->
                            BitmapDrawable(context.resources, bitmap)
                        }
                    }
            }.getOrNull()
        }
    }
    AndroidView(
        factory = {
            ImageView(it).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { it.setImageDrawable(wallpaper) },
        modifier = modifier,
    )
}

@Composable
private fun createPreviewIdp(
    columns: Int,
    rows: Int,
    hotseatColumns: Int,
    hotseatColumnsUnfolded: Int,
): InvariantDeviceProfile {
    val context = LocalContext.current
    return remember(columns, rows, hotseatColumns, hotseatColumnsUnfolded) {
        InvariantDeviceProfile(
            context,
            GridSize(
                numRows = rows,
                numColumns = columns,
                numHotseatColumns = hotseatColumns,
                numHotseatColumnsUnfolded = hotseatColumnsUnfolded.coerceAtLeast(hotseatColumns),
            ),
        )
    }
}
