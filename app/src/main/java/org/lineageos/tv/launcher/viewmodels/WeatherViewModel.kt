/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import org.lineageos.tv.launcher.weather.ApiError
import org.lineageos.tv.launcher.weather.OpenWeatherDataSource
import org.lineageos.tv.launcher.weather.openweathermap.OpenWeatherMapClient
import org.lineageos.tv.launcher.weather.models.Weather
import org.lineageos.tv.launcher.weather.openweathermap.models.RequestStatus

class WeatherViewModel : ViewModel() {
    private val openWeatherMapClient =
        OpenWeatherMapClient("https://api.openweathermap.org/", "")

    // StateFlow to hold the request status
    private val _weatherStatus = MutableStateFlow<RequestStatus<Weather, ApiError>?>(null)
    val weatherStatus: StateFlow<RequestStatus<Weather, ApiError>?> = _weatherStatus

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            // Update the state to Loading
            _weatherStatus.value = RequestStatus.Loading()

            val dataSource = OpenWeatherDataSource()
            _weatherStatus.value = dataSource.currentWeather("Tampere").last()
        }
    }
}
