/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.weather

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.lineageos.tv.launcher.weather.models.RequestStatus
import org.lineageos.tv.launcher.weather.models.WeatherResponse

class WeatherService(private val apiKey: String) {

    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getCurrentWeather(city: String): RequestStatus<WeatherResponse, String> {
        return withContext(Dispatchers.IO) {
            //try {
            // Emit loading state
            RequestStatus.Loading<WeatherResponse, String>()

            // Make the API request
            val response: WeatherResponse = client.get {
                url("https://api.openweathermap.org/data/2.5/weather")
                parameter("q", city)
                parameter("appid", apiKey)
                parameter("units", "metric")
            }.body()

            // Emit success state with the data
            RequestStatus.Success(response)
            /*} catch (e: Exception) {
                // Emit error state with the error message
                Log.e("testing", e.toString())
                Log.e("testing", e.message.toString())
                RequestStatus.Error("Failed to fetch weather data", e)
            }*/
        }
    }
}