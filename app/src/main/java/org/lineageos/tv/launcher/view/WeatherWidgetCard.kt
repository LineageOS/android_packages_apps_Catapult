/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.view

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.viewmodels.WeatherViewModel
import org.lineageos.tv.launcher.weather.openweathermap.WeatherUtils
import org.lineageos.tv.launcher.weather.openweathermap.models.RequestStatus
import kotlin.math.roundToInt

class WeatherWidgetCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Card(context, attrs, defStyleAttr) {

    // Views
    private val weatherIconImageView by lazy { findViewById<ImageView>(R.id.weatherIconImageView)!! }
    private val weatherDescriptionTextView by lazy { findViewById<TextView>(R.id.weatherDescriptionTextView)!! }
    private val temperatureTextView by lazy { findViewById<TextView>(R.id.temperatureTextView)!! }

    private val weatherViewModel: WeatherViewModel = WeatherViewModel()

    init {
        inflate(context, R.layout.weather_widget_card, this)

        stateListAnimator =
            AnimatorInflater.loadStateListAnimator(context, R.animator.app_card_state_animator)

        weatherViewModel.fetchWeather("Tampere")

        CoroutineScope(Dispatchers.Main).launch {
            weatherViewModel.weatherStatus.collect { status ->
                when (status) {
                    is RequestStatus.Loading -> {
                        weatherDescriptionTextView.text = context.getString(R.string.loading)
                    }

                    is RequestStatus.Success -> {
                        weatherIconImageView.setImageDrawable(
                            AppCompatResources.getDrawable(context, status.data.iconResId)
                        )
                        weatherDescriptionTextView.text = status.data.description
                        temperatureTextView.text = context.getString(
                            R.string.temperature_c,
                            status.data.temperature.roundToInt().toString()
                        )
                    }

                    is RequestStatus.Error -> {
                    }

                    null -> {
                    }
                }
            }
        }
    }
}
