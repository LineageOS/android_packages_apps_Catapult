/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.lineageos.tv.launcher.R

data class BatteryInfo(val percentage: Int, val isCharging: Boolean, val hasBattery: Boolean)

/**
 * Returns a Flow emitting BatteryInfo on battery changes.
 */
fun Context.batteryStatusFlow(): Flow<BatteryInfo> = callbackFlow {
    val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)

    fun sendBattery(intent: Intent?) {
        if (intent == null) return
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)

        val percent = if (level >= 0 && scale > 0) level * 100 / scale else 0
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL

        trySend(BatteryInfo(percent, isCharging, present))
    }

    sendBattery(registerReceiver(null, filter))

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            sendBattery(intent)
        }
    }
    registerReceiver(receiver, filter)

    awaitClose { unregisterReceiver(receiver) }
}

fun selectBatteryIcon(percent: Int, isCharging: Boolean): Int = when {
    isCharging && percent >= 90 -> R.drawable.ic_battery_charging_90
    isCharging && percent >= 80 -> R.drawable.ic_battery_charging_80
    isCharging && percent >= 30 -> R.drawable.ic_battery_charging_30
    isCharging -> R.drawable.ic_battery_charging_20
    percent >= 90 -> R.drawable.ic_battery_full
    percent >= 80 -> R.drawable.ic_battery_5_bar
    percent >= 60 -> R.drawable.ic_battery_4_bar
    percent >= 40 -> R.drawable.ic_battery_3_bar
    percent >= 20 -> R.drawable.ic_battery_2_bar
    else -> R.drawable.ic_battery_0_bar
}
