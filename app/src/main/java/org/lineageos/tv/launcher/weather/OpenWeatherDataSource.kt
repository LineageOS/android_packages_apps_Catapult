package org.lineageos.tv.launcher.weather

import kotlinx.coroutines.flow.asFlow
import org.lineageos.tv.launcher.utils.toRequestStatus

class OpenWeatherDataSource(apiKey: String) : WeatherDataSource {
    private val client = OpenWeatherClient(
        "https://api.openweathermap.org", apiKey
    )

    override fun currentWeather(city: String) = suspend {
        client.getCurrentWeather(city).toRequestStatus {
            this
        }
    }.asFlow()
}
