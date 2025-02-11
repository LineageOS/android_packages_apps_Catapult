/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.lineageos.tv.launcher.weather.WeatherService
import org.lineageos.tv.launcher.weather.models.RequestStatus
import org.lineageos.tv.launcher.weather.models.WeatherResponse

class WeatherViewModel : ViewModel() {
    private val weatherService = WeatherService("")

    // StateFlow to hold the request status
    private val _weatherStatus = MutableStateFlow<RequestStatus<WeatherResponse, String>?>(null)
    val weatherStatus: StateFlow<RequestStatus<WeatherResponse, String>?> = _weatherStatus

    fun fetchWeather(city: String) {
        viewModelScope.launch {
            // Update the state to Loading
            _weatherStatus.value = RequestStatus.Loading()

            // Fetch the weather data
            val result = weatherService.getCurrentWeather(city)

            // Update the state with the result
            _weatherStatus.value = result
        }
    }
}