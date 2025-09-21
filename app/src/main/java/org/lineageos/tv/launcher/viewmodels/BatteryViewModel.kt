/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.viewmodels

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.Dispatchers
import org.lineageos.tv.launcher.ext.batteryStatusFlow
import org.lineageos.tv.launcher.ext.context
import org.lineageos.tv.launcher.model.BatteryInfo

class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()

    val batteryState: StateFlow<BatteryInfo> = context.batteryStatusFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = BatteryInfo(0, isCharging = false, hasBattery = false)
        )
}