/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.preference.PreferenceManager
import androidx.tvprovider.media.tv.PreviewChannel
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.ext.hiddenChannels
import org.lineageos.tv.launcher.model.InternalChannel

@Suppress("RestrictedApi")
object Suggestions {
    fun toggleChannel(context: Context, channelId: Long, enabled: Boolean) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        sharedPreferences.hiddenChannels = sharedPreferences.hiddenChannels.toMutableSet().apply {
            if (enabled) {
                remove(channelId)
            } else {
                if (!contains(channelId)) {
                    add(channelId)
                }
            }
        }
    }

    fun getChannelTitle(context: Context, previewChannel: PreviewChannel): String {
        val appName = previewChannel.getAppName(context)
        if (appName.isEmpty()) {
            return previewChannel.displayName.toString()
        }

        // Avoid having "appName: appName" in the list title
        if (appName == previewChannel.displayName) {
            return appName
        }

        return context.resources.getString(
            R.string.channel_title, appName, previewChannel.displayName
        )
    }

    fun <T, K> List<T>.orderSuggestions(orderIds: List<K>, idSelector: (T) -> K?): List<T> {
        if (orderIds.isEmpty()) {
            val (presentItems, remainingItems) = this.partition {
                idSelector(it) == InternalChannel.ALL_APPS.id
            }
            return remainingItems + presentItems
        }

        val (presentItems, remainingItems) = this.partition { idSelector(it) in orderIds }
        val sortedPresentItems = presentItems.sortedBy { orderIds.indexOf(idSelector(it)) }
        return sortedPresentItems + remainingItems
    }

    private fun PreviewChannel.getAppName(context: Context): String {
        val packageManager: PackageManager = context.packageManager
        return try {
            val applicationInfo = packageManager.getApplicationInfo(this.packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            ""
        }
    }
}
