/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import org.lineageos.tv.launcher.utils.AppManager.uninstallable

class AppInfo : Launchable {
    var banner: Drawable?
    private val packageManager: PackageManager = context.packageManager
    private val applicationInfo: ApplicationInfo

    constructor(resolveInfo: ResolveInfo, context: Context) : super(
        resolveInfo.loadLabel(context.packageManager).toString(),
        resolveInfo.activityInfo.packageName,
        resolveInfo.loadIcon(context.packageManager),
        context
    ) {
        applicationInfo = resolveInfo.activityInfo.applicationInfo
        banner = resolveInfo.activityInfo.loadBanner(packageManager)
        if (banner == null) {
            banner = resolveInfo.activityInfo.applicationInfo.loadBanner(packageManager)
        }
    }

    constructor(app: ApplicationInfo, context: Context) : super(
        app.loadLabel(context.packageManager).toString(),
        app.packageName,
        app.loadIcon(context.packageManager),
        context
    ) {
        applicationInfo = app
        banner = app.loadBanner(packageManager)
    }

    override fun setIntent() = packageManager.getLeanbackLaunchIntentForPackage(packageName)
        ?: packageManager.getLaunchIntentForPackage(packageName)

    fun isUninstallable(): Boolean {
        return uninstallable(applicationInfo, context)
    }
}
