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
import org.lineageos.tv.launcher.weather.OpenWeatherClient
import org.lineageos.tv.launcher.weather.OpenWeatherDataSource
import org.lineageos.tv.launcher.weather.models.RequestStatus
import org.lineageos.tv.launcher.weather.models.WeatherResponse

class WeatherViewModel : ViewModel() {
    private val weatherService =
        OpenWeatherClient("https://api.openweathermap.org/", "")

    // StateFlow to hold the request status
    private val _weatherStatus = MutableStateFlow<RequestStatus<WeatherResponse, ApiError>?>(null)
    val weatherStatus: StateFlow<RequestStatus<WeatherResponse, ApiError>?> = _weatherStatus

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            // Update the state to Loading
            _weatherStatus.value = RequestStatus.Loading()

            val dataSource = OpenWeatherDataSource()
            _weatherStatus.value = dataSource.currentWeather("Tampere").last()
        }
    }
}
