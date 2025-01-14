/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.notification

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationManagerCompat

object NotificationUtils {
    fun shouldAutoCancel(notification: Notification): Boolean {
        val flags: Int = notification.flags
        if ((flags and Notification.FLAG_AUTO_CANCEL) != Notification.FLAG_AUTO_CANCEL) {
            return false
        }
        if ((flags and Notification.FLAG_FOREGROUND_SERVICE) != 0) {
            return false
        }
        return true
    }

    fun notificationPermissionGranted(context: Context): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(context)
            .contains(context.packageName)
    }
}
