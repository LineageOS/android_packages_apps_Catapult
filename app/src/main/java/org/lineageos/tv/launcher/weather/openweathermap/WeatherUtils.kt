/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.weather.openweathermap

import org.lineageos.tv.launcher.R

class WeatherUtils {
    companion object {
        fun getWeatherResId(weatherId: Int): Int {
            return when (weatherId) {
                in 200..232 -> R.drawable.ic_thunder
                in 300..321 -> R.drawable.ic_rain
                in 500..531 -> R.drawable.ic_rain
                in 600..622 -> R.drawable.ic_snow
                701 -> R.drawable.ic_mist
                711 -> R.drawable.ic_foggy
                721 -> R.drawable.ic_hail
                731 -> R.drawable.ic_hail
                741 -> R.drawable.ic_foggy
                751 -> R.drawable.ic_hail
                761 -> R.drawable.ic_hail
                762 -> R.drawable.ic_hail
                771 -> R.drawable.ic_thunder
                781 -> R.drawable.ic_thunder
                800 -> R.drawable.ic_sunny
                in 801..804 -> R.drawable.ic_cloudy
                else -> R.drawable.ic_sunny
            }
        }
    }
}
