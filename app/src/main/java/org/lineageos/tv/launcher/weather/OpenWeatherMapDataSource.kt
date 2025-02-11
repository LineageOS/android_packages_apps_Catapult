/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.weather

import kotlinx.coroutines.flow.asFlow
import org.lineageos.tv.launcher.utils.toRequestStatus
import org.lineageos.tv.launcher.weather.models.Weather
import org.lineageos.tv.launcher.weather.openweathermap.OpenWeatherMapClient
import org.lineageos.tv.launcher.weather.openweathermap.WeatherUtils
import org.lineageos.tv.launcher.weather.openweathermap.models.WeatherResponse

class OpenWeatherDataSource : WeatherDataSource {
    private val client = OpenWeatherMapClient(
        "https://api.openweathermap.org", "")

    override fun currentWeather(city: String) = suspend {
        client.getCurrentWeather(city).toRequestStatus {
            this.toWeather()
        }
    }.asFlow()

    private fun WeatherResponse.toWeather() = Weather(
        temperature = main.temp,
        description = weather[0].main,
        iconResId = weatherIcon(weather[0].id)
    )

    private fun weatherIcon(weatherId: Int): Int {
        return WeatherUtils.getWeatherResId(weatherId)
    }
}
