/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.notification

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.service.notification.NotificationListenerService.RankingMap
import android.service.notification.StatusBarNotification
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

sealed class ServiceConnectionState {
    object Connected : ServiceConnectionState()
    object Disconnected : ServiceConnectionState()
    data class Notifications(
        val notifications: Map<String, StatusBarNotification>,
        val currentRanking: RankingMap?
    ) : ServiceConnectionState()
}

class NotificationServiceConnection : ServiceConnection {
    // SharedFlow to emit updates or callbacks
    private val _serviceUpdates = MutableSharedFlow<ServiceConnectionState>(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val serviceUpdates: SharedFlow<ServiceConnectionState> = _serviceUpdates

    private var notificationListener: TvNotificationListener? = null
    private var notificationUpdateListener: TvNotificationListener.NotificationUpdateListener? =
        null

    override fun onServiceConnected(className: ComponentName, binder: IBinder) {
        notificationListener = (binder as TvNotificationListener.LocalBinder).getService()
        _serviceUpdates.tryEmit(ServiceConnectionState.Connected)

        // Create and register a listener for updates
        notificationUpdateListener = object : TvNotificationListener.NotificationUpdateListener {
            override fun onNotificationsChanged() {
                emitNotificationUpdate()
            }
        }
        notificationListener?.addNotificationUpdateListener(notificationUpdateListener)

        emitNotificationUpdate()
    }

    override fun onServiceDisconnected(name: ComponentName?) {
        notificationListener?.removeNotificationUpdateListener(notificationUpdateListener)
        notificationListener = null
        notificationUpdateListener = null
        _serviceUpdates.tryEmit(ServiceConnectionState.Disconnected)
    }

    private fun emitNotificationUpdate() {
        val notifications = notificationListener?.getNotifications() ?: emptyMap()
        val ranking = notificationListener?.currentRanking
        _serviceUpdates.tryEmit(ServiceConnectionState.Notifications(notifications, ranking))
    }

    fun cancelNotification(key: String) {
        notificationListener?.cancelNotification(key)
    }
}
