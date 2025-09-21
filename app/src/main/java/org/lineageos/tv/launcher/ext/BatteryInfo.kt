/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import org.lineageos.tv.launcher.R

fun getIcon(percent: Int, isCharging: Boolean): Int = when {
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
