/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.lineageos.tv.launcher.model.BatteryInfo

private fun Context.resolveAttribute(@AttrRes attribute: Int) = TypedValue().also {
    theme.resolveAttribute(attribute, it, true)
}

fun Context.getAttributeResourceId(@AttrRes attribute: Int) = resolveAttribute(attribute).resourceId

@ColorInt
fun Context.getAttributeColor(@AttrRes attribute: Int) = resolveAttribute(attribute).let {
    require(it.isColorType)
    it.data
}

fun Context.batteryStatusFlow(): Flow<BatteryInfo?> = callbackFlow {
    val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    fun sendBattery(intent: Intent?) {
        if (intent == null) return
        if (!intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)) {
            trySend(null)
            return
        }
        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val present = intent.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false)
        val percent = if (level >= 0 && scale > 0) level * 100 / scale else 0
        val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL
        trySend(BatteryInfo(percent, isCharging, present))
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            sendBattery(intent)
        }
    }

    sendBattery(registerReceiver(receiver, filter))
    awaitClose { unregisterReceiver(receiver) }
}
