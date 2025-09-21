/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.model.BatteryInfo

fun BatteryInfo.getIcon(): Int = when {
    isCharging && percentage >= 90 -> R.drawable.ic_battery_charging_90
    isCharging && percentage >= 80 -> R.drawable.ic_battery_charging_80
    isCharging && percentage >= 30 -> R.drawable.ic_battery_charging_30
    isCharging -> R.drawable.ic_battery_charging_20
    percentage >= 90 -> R.drawable.ic_battery_full
    percentage >= 80 -> R.drawable.ic_battery_5_bar
    percentage >= 60 -> R.drawable.ic_battery_4_bar
    percentage >= 40 -> R.drawable.ic_battery_3_bar
    percentage >= 20 -> R.drawable.ic_battery_2_bar
    else -> R.drawable.ic_battery_0_bar
}
