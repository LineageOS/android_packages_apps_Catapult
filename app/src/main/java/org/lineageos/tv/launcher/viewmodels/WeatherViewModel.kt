/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.viewmodels

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import org.lineageos.tv.launcher.ext.weatherApiKey
import org.lineageos.tv.launcher.ext.weatherCity
import org.lineageos.tv.launcher.weather.ApiError
import org.lineageos.tv.launcher.weather.OpenWeatherDataSource
import org.lineageos.tv.launcher.weather.models.RequestStatus
import org.lineageos.tv.launcher.weather.models.WeatherResponse

class WeatherViewModel(private val sharedPreferences: SharedPreferences) : ViewModel() {
    // StateFlow to hold the request status
    private val _weatherStatus = MutableStateFlow<RequestStatus<WeatherResponse, ApiError>?>(null)
    val weatherStatus: StateFlow<RequestStatus<WeatherResponse, ApiError>?> = _weatherStatus

    fun fetchWeather() {
        viewModelScope.launch {
            val apiKey = sharedPreferences.weatherApiKey
            val city = sharedPreferences.weatherCity

            when {
                apiKey == null -> _weatherStatus.value =
                    RequestStatus.Error(ApiError.INVALID_CREDENTIALS)

                city == null -> _weatherStatus.value = RequestStatus.Error(ApiError.NOT_FOUND)
                else -> {
                    val dataSource = OpenWeatherDataSource(apiKey)
                    _weatherStatus.value = RequestStatus.Loading()
                    _weatherStatus.value = dataSource.currentWeather(city).last()
                }
            }
        }
    }

    fun setCredentials(apiKey: String, city: String) {
        sharedPreferences.weatherApiKey = apiKey
        sharedPreferences.weatherCity = city
        fetchWeather()
    }
}
