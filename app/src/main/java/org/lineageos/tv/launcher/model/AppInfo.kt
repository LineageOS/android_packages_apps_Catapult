/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.ResolveInfo

class AppInfo : LeanbackAppInfo {

    constructor(resolveInfo: ResolveInfo, context: Context) : super(resolveInfo, context)

    constructor(app: ApplicationInfo, context: Context) : super(app, context)

    override fun setIntent() = packageManager.getLaunchIntentForPackage(packageName)

    companion object {
        fun create(context: Context, appId: String): LeanbackAppInfo {
            val leanbackAppInfo =
                LeanbackAppInfo(context.packageManager.getApplicationInfo(appId, 0), context)

            return if (leanbackAppInfo.launchIntent == null) {
                // Fall back to AppInfo if leanback intent is unavailable
                AppInfo(context.packageManager.getApplicationInfo(appId, 0), context)
            } else {
                leanbackAppInfo
            }
        }
    }
}
