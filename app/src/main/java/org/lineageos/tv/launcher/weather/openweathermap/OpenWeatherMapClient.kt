/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.weather.openweathermap

import android.net.Uri
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.lineageos.tv.launcher.utils.Api
import org.lineageos.tv.launcher.utils.ApiRequest
import org.lineageos.tv.launcher.weather.openweathermap.models.WeatherResponse

class OpenWeatherMapClient(
    server: String, private val apiKey: String,
    cache: Cache? = null
) {
    private val serverUri = Uri.parse(server)

    private val okHttpClient = OkHttpClient.Builder()
        .cache(cache)
        .build()

    private val api = Api(okHttpClient, serverUri)

    suspend fun getCurrentWeather(city: String) = ApiRequest.get<WeatherResponse>(
        listOf("data", "2.5", "weather"),
        queryParameters = listOf(
            "q" to city,
            "appid" to apiKey,
            "units" to "metric",
        ),
    ).execute(api)
}
