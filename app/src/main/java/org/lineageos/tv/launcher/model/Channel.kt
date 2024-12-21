/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

import android.content.Context
import androidx.tvprovider.media.tv.PreviewChannel
import org.lineageos.tv.launcher.R

class Channel(
    val id: Long,
    val title: String,
    val previewChannel: PreviewChannel? = null,
) {
    companion object {
        fun getFavoritesAppsChannel(context: Context) = Channel(
            InternalChannel.FAVORITE_APPS.id,
            context.getString(R.string.favorites)
        )

        fun getAllAppsChannel(context: Context) = Channel(
            InternalChannel.ALL_APPS.id,
            context.getString(R.string.all_apps)
        )

        fun getWatchNextChannel(context: Context) = Channel(
            InternalChannel.WATCH_NEXT.id,
            context.getString(R.string.watch_next)
        )
    }
}
