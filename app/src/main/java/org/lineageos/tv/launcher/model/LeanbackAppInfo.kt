/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.Canvas
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.utils.AppManager.uninstallable

open class LeanbackAppInfo : Launchable {
    var banner: Drawable?
    protected val packageManager: PackageManager = context.packageManager
    private val applicationInfo: ApplicationInfo

    constructor(resolveInfo: ResolveInfo, context: Context) : super(
        resolveInfo.loadLabel(context.packageManager).toString(),
        resolveInfo.activityInfo.packageName,
        resolveInfo.loadIcon(context.packageManager),
        context
    ) {
        applicationInfo = resolveInfo.activityInfo.applicationInfo
        val rawBanner = resolveInfo.activityInfo.loadBanner(packageManager)
            ?: resolveInfo.activityInfo.applicationInfo.loadBanner(packageManager)
        banner = rawBanner?.let { flattenAdaptiveBanner(it, context) }
    }

    constructor(app: ApplicationInfo, context: Context) : super(
        app.loadLabel(context.packageManager).toString(),
        app.packageName,
        app.loadIcon(context.packageManager),
        context
    ) {
        applicationInfo = app
        banner = app.loadBanner(packageManager)?.let { flattenAdaptiveBanner(it, context) }
    }

    override fun setIntent() = packageManager.getLeanbackLaunchIntentForPackage(packageName)

    fun isUninstallable(): Boolean {
        return uninstallable(applicationInfo, context)
    }

    private fun flattenAdaptiveBanner(drawable: Drawable, context: Context): Drawable {
        if (drawable !is AdaptiveIconDrawable) {
            return drawable
        }

        val width = context.resources.getDimensionPixelSize(R.dimen.app_card_width)
        val height = context.resources.getDimensionPixelSize(R.dimen.app_card_height)

        val bitmap = createBitmap(width, height)
        val canvas = Canvas(bitmap)

        drawable.background?.apply {
            setBounds(0, 0, width, height)
            draw(canvas)
        }
        drawable.foreground?.apply {
            setBounds(0, 0, width, height)
            draw(canvas)
        }

        return bitmap.toDrawable(context.resources)
    }
}
