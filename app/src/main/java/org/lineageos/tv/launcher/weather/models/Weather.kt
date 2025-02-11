/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.weather.models

data class Weather(
    val temperature: Double,
    val description: String,
    val iconResId: Int
)