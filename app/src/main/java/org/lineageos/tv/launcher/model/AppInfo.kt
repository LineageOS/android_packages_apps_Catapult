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
}
