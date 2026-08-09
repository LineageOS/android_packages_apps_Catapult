/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.notification

import android.app.Notification
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.CopyOnWriteArrayList


class TvNotificationListener : NotificationListenerService() {
    private val activeTvNotifications: ConcurrentMap<String, StatusBarNotification> =
        ConcurrentHashMap()
    private var isListenerConnected: Boolean = false
    private var listeners: CopyOnWriteArrayList<NotificationUpdateListener> = CopyOnWriteArrayList()

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        if (shouldShowNotification(sbn.notification)) {
            activeTvNotifications[sbn.key] = sbn
            listeners.forEach { it.onNotificationsChanged() }
        } else {
            Log.d(TAG, "Ignoring notification: not available on TV.")
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        activeTvNotifications.remove(sbn.key)
        listeners.forEach { it.onNotificationsChanged() }
    }

    override fun onBind(intent: Intent): IBinder {
        return if (ACTION_LOCAL_BINDING == intent.action)
            LocalBinder()
        else
            super.onBind(intent)!!
    }

    override fun onListenerConnected() {
        activeNotifications.forEach {
            if (shouldShowNotification(it.notification)) {
                activeTvNotifications[it.key] = it
            }
        }

        isListenerConnected = true
    }

    override fun onListenerDisconnected() {
        isListenerConnected = false
    }

    fun getNotifications(): Map<String, StatusBarNotification> {
        return activeTvNotifications
    }

    fun addNotificationUpdateListener(listener: NotificationUpdateListener?) {
        listeners.add(listener)
    }

    fun removeNotificationUpdateListener(listener: NotificationUpdateListener?) {
        listeners.remove(listener)
    }

    private fun shouldShowNotification(notification: Notification): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.VANILLA_ICE_CREAM ||
                Notification.TvExtender(notification).isAvailableOnTv
    }

    inner class LocalBinder : Binder() {
        fun getService(): TvNotificationListener {
            return this@TvNotificationListener
        }
    }

    interface NotificationUpdateListener {
        fun onNotificationsChanged()
    }

    companion object {
        const val ACTION_LOCAL_BINDING: String = "local_binding"
        const val TAG: String = "TvNotificationListener"
    }
}
