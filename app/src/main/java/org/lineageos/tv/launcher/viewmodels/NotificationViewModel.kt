/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.viewmodels

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import org.lineageos.tv.launcher.notification.NotificationServiceConnection
import org.lineageos.tv.launcher.notification.ServiceConnectionState
import org.lineageos.tv.launcher.notification.TvNotificationListener

class NotificationViewModel(application: Application) : AndroidViewModel(application) {
    private val serviceConnection = NotificationServiceConnection()

    fun bindService(context: Context) {
        val intent = Intent(context, TvNotificationListener::class.java).apply {
            action = TvNotificationListener.ACTION_LOCAL_BINDING
        }
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    fun unbindService(context: Context) {
        context.unbindService(serviceConnection)
    }

    val state = serviceConnection.serviceUpdates.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        ServiceConnectionState.Disconnected
    )

    fun cancelNotification(key: String) {
        serviceConnection.cancelNotification(key)
    }
}
