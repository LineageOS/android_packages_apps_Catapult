/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.weather

import kotlinx.coroutines.flow.Flow
import org.lineageos.tv.launcher.weather.models.RequestStatus
import org.lineageos.tv.launcher.weather.models.WeatherResponse

typealias WeatherRequestStatus<T> = RequestStatus<T, ApiError>

/**
 * A data source for weather.
 */
interface WeatherDataSource {
    /**
     * Get the current weather.
     * @param city: The city name.
     */
    fun currentWeather(city: String): Flow<WeatherRequestStatus<WeatherResponse>>
}
