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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.android.launcher3.R

@Composable
fun UwUQsbUi(
    onClick: () -> Unit,
    onVoiceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(50)
    val searchLabel = stringResource(R.string.label_search)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh, shape)
            .clickable(
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                contentDescription = searchLabel
                role = Role.Button
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        QsbIcon(
            imageVector = Icons.Filled.Search,
            description = searchLabel,
            onClick = onClick,
            modifier = Modifier.width(dimensionResource(R.dimen.uwu_qsb_icon_width)),
        )
        Box(modifier = Modifier.weight(1f))
        QsbIcon(
            imageVector = Icons.Filled.Mic,
            description = stringResource(R.string.label_voice_search),
            onClick = onVoiceClick,
            modifier = Modifier.width(dimensionResource(R.dimen.uwu_qsb_icon_width)),
        )
    }
}

@Composable
private fun QsbIcon(
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clickable(role = Role.Button, onClick = onClick)
            .padding(dimensionResource(R.dimen.uwu_qsb_icon_padding)),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp),
        )
    }
}
