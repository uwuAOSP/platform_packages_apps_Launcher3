/*
 * SPDX-FileCopyrightText: The uwuAOSP Project
 * SPDX-License-Identifier: Apache-2.0
 */

package com.android.launcher3.wallpaper

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.android.launcher3.util.Executors
import java.io.IOException

internal class WallpaperRecentsRepository(private val context: Context) {

    data class Wallpaper(
        val id: String,
        val placeholderColor: Int,
        val title: String?,
    )

    fun loadRecentWallpapers(callback: (List<Wallpaper>) -> Unit) {
        Executors.UI_HELPER_EXECUTOR.execute {
            val wallpapers = try {
                context.contentResolver.query(
                    LIST_URI.buildUpon().appendPath(HOME_SCREEN).build(),
                    COLUMNS,
                    null,
                    null,
                    null,
                )?.use { cursor ->
                    buildList {
                        val idColumn = cursor.getColumnIndex(COLUMN_ID)
                        val colorColumn = cursor.getColumnIndex(COLUMN_PLACEHOLDER_COLOR)
                        val titleColumn = cursor.getColumnIndex(COLUMN_TITLE)
                        while (cursor.moveToNext()) {
                            if (idColumn < 0) continue
                            add(
                                Wallpaper(
                                    id = cursor.getString(idColumn),
                                    placeholderColor =
                                        if (colorColumn >= 0) cursor.getInt(colorColumn) else 0,
                                    title = if (titleColumn >= 0) cursor.getString(titleColumn)
                                        else null,
                                )
                            )
                        }
                    }
                } ?: emptyList()
            } catch (_: Exception) {
                emptyList()
            }
            Executors.MAIN_EXECUTOR.execute { callback(wallpapers) }
        }
    }

    fun loadThumbnail(wallpaper: Wallpaper, callback: (Bitmap?) -> Unit) {
        Executors.UI_HELPER_EXECUTOR.execute {
            val bitmap = try {
                val uri = THUMBNAIL_URI.buildUpon()
                    .appendPath(wallpaper.id)
                    .appendQueryParameter(DESTINATION, HOME_SCREEN)
                    .build()
                context.contentResolver.openFile(uri, "r", null)?.use { descriptor ->
                    BitmapFactory.decodeFileDescriptor(descriptor.fileDescriptor)
                }
            } catch (_: IOException) {
                null
            } catch (_: SecurityException) {
                null
            }
            Executors.MAIN_EXECUTOR.execute { callback(bitmap) }
        }
    }

    fun apply(wallpaper: Wallpaper, callback: (Boolean) -> Unit) {
        Executors.UI_HELPER_EXECUTOR.execute {
            val values = ContentValues().apply {
                put(COLUMN_ID, wallpaper.id)
                put(COLUMN_SCREEN, HOME_SCREEN)
                put(COLUMN_ENTRY_POINT, ENTRY_POINT_QUICK_SWITCHER)
            }
            val success = try {
                context.contentResolver.update(SET_URI, values, null, null) > 0
            } catch (_: Exception) {
                false
            }
            Executors.MAIN_EXECUTOR.execute { callback(success) }
        }
    }

    companion object {
        private const val AUTHORITY = "com.google.android.apps.wallpaper.recents"
        private const val HOME_SCREEN = "home_screen"
        private const val DESTINATION = "destination"
        private const val COLUMN_ID = "id"
        private const val COLUMN_PLACEHOLDER_COLOR = "placeholder_color"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_SCREEN = "screen"
        private const val COLUMN_ENTRY_POINT = "set_wallpaper_entry_point"
        private const val ENTRY_POINT_QUICK_SWITCHER = 3
        private val BASE_URI = Uri.parse("content://$AUTHORITY")
        private val LIST_URI = BASE_URI.buildUpon().appendPath("list_recent").build()
        private val THUMBNAIL_URI = BASE_URI.buildUpon().appendPath("thumb").build()
        private val SET_URI = BASE_URI.buildUpon().appendPath("set_recent_wallpaper").build()
        private val COLUMNS = arrayOf(COLUMN_ID, COLUMN_PLACEHOLDER_COLOR, COLUMN_TITLE)

        @JvmStatic
        fun isAvailable(context: Context): Boolean = try {
            context.packageManager.resolveContentProvider(AUTHORITY, 0) != null
        } catch (_: Exception) {
            false
        }
    }
}
