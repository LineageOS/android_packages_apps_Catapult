/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.repository

import android.content.Context
import org.lineageos.tv.launcher.flow.InstalledAppsFlow
import org.lineageos.tv.launcher.flow.InstalledWidgetsFlow
import org.lineageos.tv.launcher.flow.PreviewChannelsFlow
import org.lineageos.tv.launcher.flow.PreviewProgramsFlow
import org.lineageos.tv.launcher.flow.SharedPreferencesFlows
import org.lineageos.tv.launcher.flow.WatchNextProgramsFlow

object LauncherRepository {
    fun installedApps(
        context: Context,
    ) = InstalledAppsFlow(context).flow()

    fun previewChannels(
        context: Context,
    ) = PreviewChannelsFlow(context).flowData()

    fun previewPrograms(
        context: Context,
        channelId: Long,
    ) = PreviewProgramsFlow(context, channelId).flowData()

    fun watchNextPrograms(
        context: Context,
    ) = WatchNextProgramsFlow(context).flowData()

    fun favorites(
        context: Context,
    ) = SharedPreferencesFlows(context).favoritesFlow()

    fun installedWidgets(
        context: Context,
    ) = InstalledWidgetsFlow(context).flow()

    fun knownChannels(
        context: Context,
    ) = SharedPreferencesFlows(context).knownChannelsFlow()

    fun hiddenChannels(
        context: Context,
    ) = SharedPreferencesFlows(context).hiddenChannelsFlow()
}
