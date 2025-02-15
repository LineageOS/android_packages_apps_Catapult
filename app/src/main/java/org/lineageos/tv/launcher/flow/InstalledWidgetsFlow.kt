/*
 * SPDX-FileCopyrightText: 2023-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.flow

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.mapNotNull
import org.lineageos.tv.launcher.ext.broadcastFlow
import org.lineageos.tv.launcher.model.WidgetInfo

class InstalledWidgetsFlow(private val context: Context) {
    private val appWidgetManager: AppWidgetManager = AppWidgetManager.getInstance(context)

    fun flow() = IntentFilter().apply {
        actions.forEach {
            addAction(it)
        }
        addDataScheme(PACKAGE_SCHEME)
    }.broadcastFlow(context, true).mapNotNull { intent ->
        intent?.also {
            val data = it.data ?: return@mapNotNull null

            if (!actions.contains(it.action)) {
                return@mapNotNull null
            }

            if (data.scheme != PACKAGE_SCHEME) {
                return@mapNotNull null
            }
        }

        appWidgetManager.installedProviders.map {
            WidgetInfo(it, context)
        }
    }

    companion object {
        private const val PACKAGE_SCHEME = "package"

        private val actions = listOf(
            Intent.ACTION_PACKAGE_ADDED,
            Intent.ACTION_PACKAGE_FULLY_REMOVED,
        )
    }
}
