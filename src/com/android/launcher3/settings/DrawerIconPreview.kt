/*
 * SPDX-FileCopyrightText: The uwuAOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */
package com.android.launcher3.settings

import android.content.pm.LauncherApps
import android.graphics.drawable.Drawable
import android.os.Process
import android.util.TypedValue
import android.view.Gravity
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.android.launcher3.InvariantDeviceProfile
import com.android.launcher3.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private data class DrawerPreviewItem(
    val key: String,
    val label: String,
    val icon: Drawable,
)

@Composable
internal fun DrawerIconPreview(
    idp: InvariantDeviceProfile,
    iconSizeFactor: Float,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val apps by produceState<List<DrawerPreviewItem>>(emptyList(), context) {
        value = withContext(Dispatchers.IO) {
            runCatching {
                context.getSystemService(LauncherApps::class.java)
                    ?.getActivityList(null, Process.myUserHandle())
                    ?.sortedBy { it.label.toString().lowercase() }
                    ?.take(12)
                    ?.map { activity ->
                        DrawerPreviewItem(
                            key = activity.componentName.flattenToString(),
                            label = activity.label.toString(),
                            icon = activity.getBadgedIcon(
                                context.resources.displayMetrics.densityDpi
                            ),
                        )
                    }
                    .orEmpty()
            }.getOrDefault(emptyList())
        }
    }
    val columns = idp.numAllAppsColumns.coerceAtLeast(1)

    BoxWithConstraints(
        modifier = modifier
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh),
    ) {
        val isPortrait = configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
        val deviceProfile = idp.supportedProfiles.firstOrNull { profile ->
            profile.deviceProperties.isLandscape != isPortrait
        } ?: idp.supportedProfiles.firstOrNull()
        val screenWidth = deviceProfile?.deviceProperties?.widthPx?.toFloat() ?: 1f
        val screenHeight = deviceProfile?.deviceProperties?.heightPx?.toFloat() ?: 1f
        val previewScale = with(density) {
            minOf(maxWidth.toPx() / screenWidth, maxHeight.toPx() / screenHeight)
                .coerceIn(0.3f, 0.8f)
        }
        val iconSize =
            idp.allAppsIconSize[0].dp * previewScale * iconSizeFactor.coerceIn(0.5f, 1.5f)
        val labelSize = idp.allAppsIconTextSize[0].sp * previewScale
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp * previewScale, bottom = 6.dp * previewScale),
                contentAlignment = Alignment.Center,
            ) {
                Surface(
                    modifier = Modifier
                        .width(28.dp * previewScale)
                        .height(4.dp * previewScale),
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                    shape = MaterialTheme.shapes.small,
                ) {}
            }
            AndroidView(
                factory = { viewContext ->
                    TextView(viewContext).apply {
                        background = viewContext.getDrawable(R.drawable.bg_all_apps_searchbox)
                        hint = viewContext.getString(R.string.all_apps_search_bar_hint)
                        setTextColor(viewContext.getColor(R.color.materialColorOnSurfaceVariant))
                        setHintTextColor(viewContext.getColor(R.color.materialColorOnSurfaceVariant))
                        gravity = Gravity.CENTER_VERTICAL or Gravity.START
                        maxLines = 1
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f * previewScale)
                        val searchIcon = viewContext.getDrawable(R.drawable.ic_allapps_search)
                            ?.mutate()
                        searchIcon?.setTint(currentTextColor)
                        val iconSize = (24f * previewScale * resources.displayMetrics.density).toInt()
                        searchIcon?.setBounds(0, 0, iconSize, iconSize)
                        setCompoundDrawablesRelative(searchIcon, null, null, null)
                        val iconWidth = viewContext.resources
                            .getDimensionPixelSize(R.dimen.uwu_qsb_icon_width) * previewScale
                        val scaledIconWidth = iconWidth.toInt()
                        compoundDrawablePadding = ((scaledIconWidth - iconSize) / 2).coerceAtLeast(0)
                        val horizontalPadding = ((scaledIconWidth - iconSize) / 4).coerceAtLeast(0)
                        setPadding(horizontalPadding, 0, horizontalPadding, 0)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp * previewScale, vertical = 4.dp * previewScale)
                    .height(64.dp * previewScale),
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = 8.dp * previewScale,
                    vertical = 6.dp * previewScale,
                ),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                items(apps, key = { it.key }) { app ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp * previewScale),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        AndroidView(
                            factory = { viewContext ->
                                ImageView(viewContext).apply {
                                    scaleType = ImageView.ScaleType.FIT_CENTER
                                }
                            },
                            update = { it.setImageDrawable(app.icon) },
                            modifier = Modifier.size(iconSize),
                        )
                        Text(
                            text = app.label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = 2.dp * previewScale,
                                    vertical = 2.dp * previewScale,
                                ),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = labelSize,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}
