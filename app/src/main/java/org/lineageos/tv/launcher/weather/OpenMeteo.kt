/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.weather

import android.net.Uri
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.lineageos.tv.launcher.R
import java.net.URL

data class Weather(
    val temperatureCelsius: Double,
    @StringRes val descriptionResId: Int,
    @DrawableRes val iconResId: Int,
)

object OpenMeteo {
    private const val LOG_TAG = "OpenMeteo"

    private const val GEOCODING_URL = "https://geocoding-api.open-meteo.com/v1/search"
    private const val FORECAST_URL = "https://api.open-meteo.com/v1/forecast"
    private const val TIMEOUT_MS = 10_000

    /**
     * Get the current weather.
     * @param city The city name.
     * @return The weather, or null if the city is unknown or the request failed.
     */
    suspend fun currentWeather(city: String) = withContext(Dispatchers.IO) {
        runCatching {
            val location = get(
                GEOCODING_URL.toUri().buildUpon()
                    .appendQueryParameter("name", city)
                    .appendQueryParameter("count", "1")
                    .build()
            ).getJSONArray("results").getJSONObject(0)

            val current = get(
                FORECAST_URL.toUri().buildUpon()
                    .appendQueryParameter("latitude", location.getDouble("latitude").toString())
                    .appendQueryParameter("longitude", location.getDouble("longitude").toString())
                    .appendQueryParameter("current", "temperature_2m,weather_code")
                    .build()
            ).getJSONObject("current")

            val (descriptionResId, iconResId) = describe(current.getInt("weather_code"))
            Weather(current.getDouble("temperature_2m"), descriptionResId, iconResId)
        }.onFailure {
            Log.w(LOG_TAG, "Failed to get the weather", it)
        }.getOrNull()
    }

    private fun get(uri: Uri): JSONObject {
        val connection = URL(uri.toString()).openConnection().apply {
            connectTimeout = TIMEOUT_MS
            readTimeout = TIMEOUT_MS
        }

        return connection.getInputStream().bufferedReader().use { JSONObject(it.readText()) }
    }

    /**
     * Map a WMO weather interpretation code to its description and icon.
     */
    private fun describe(code: Int) = when (code) {
        0, 1 -> R.string.weather_clear to R.drawable.ic_sunny
        45, 48 -> R.string.weather_fog to R.drawable.ic_foggy
        in 51..67, in 80..82 -> R.string.weather_rain to R.drawable.ic_rain
        in 71..77, 85, 86 -> R.string.weather_snow to R.drawable.ic_snow
        in 95..99 -> R.string.weather_thunderstorm to R.drawable.ic_thunder
        else -> R.string.weather_cloudy to R.drawable.ic_cloudy
    }
}
