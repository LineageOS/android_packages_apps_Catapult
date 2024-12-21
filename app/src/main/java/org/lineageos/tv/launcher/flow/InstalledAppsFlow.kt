/*
 * SPDX-FileCopyrightText: 2023-2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.flow

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import kotlinx.coroutines.flow.mapNotNull
import org.lineageos.tv.launcher.ext.broadcastFlow
import org.lineageos.tv.launcher.model.AppInfo

class InstalledAppsFlow(private val context: Context) {
    private val packageManager = context.packageManager
    private val leanbackLauncherIntent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER)
    }
    private val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_LAUNCHER)
    }

    fun flow() = IntentFilter().apply {
        actions.forEach {
            addAction(it)
        }
        addDataScheme(PACKAGE_SCHEME)
    }.broadcastFlow(context, true).mapNotNull { intent ->
        // If we have a non-null intent, check it, else trigger update anyway
        intent?.also {
            val data = it.data ?: return@mapNotNull null

            if (!actions.contains(it.action)) {
                return@mapNotNull null
            }

            if (data.scheme != PACKAGE_SCHEME) {
                return@mapNotNull null
            }
        }

        val leanbackLauncherActivities =
            packageManager.queryIntentActivities(leanbackLauncherIntent, 0)
                .mapNotNull { resolveInfo ->
            AppInfo(resolveInfo, context)
        }
        val launcherActivities =
            packageManager.queryIntentActivities(launcherIntent, 0).mapNotNull { resolveInfo ->
                AppInfo(resolveInfo, context)
            }

        // distinctBy keeps the first occurrence
        (leanbackLauncherActivities + launcherActivities).distinctBy {
            it.launchIntent?.resolveActivityInfo(
                packageManager,
                0
            )?.name
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
