/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context

class WidgetInfo(val appWidgetProviderInfo: AppWidgetProviderInfo, context: Context) :
    Launchable(
        appWidgetProviderInfo.loadLabel(context.packageManager),
        appWidgetProviderInfo.providerInfo.packageName,
        appWidgetProviderInfo.loadIcon(context, 10),
        context
    ) {

    val provider = appWidgetProviderInfo.provider

    companion object {
        const val WIDGET_PREFIX = "WIDGET_"

        fun create(context: Context, savedPackageName: String): WidgetInfo? {
            val packageName = savedPackageName.removePrefix(WIDGET_PREFIX)
            val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)
            val providerInfo =
                appWidgetManager.installedProviders.find { it.providerInfo.packageName == packageName }
            return providerInfo?.let { WidgetInfo(it, context) }
        }
    }
}
