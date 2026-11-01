/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.util.LocalePreferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.ext.WEATHER_CITY
import org.lineageos.tv.launcher.ext.valueFlow
import org.lineageos.tv.launcher.ext.weatherCity
import org.lineageos.tv.launcher.weather.OpenMeteo
import org.lineageos.tv.launcher.weather.Weather
import kotlin.math.roundToInt

class WeatherWidgetCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : WidgetCard(context, attrs, defStyleAttr) {
    // Views
    private val weatherIconImageView by lazy { findViewById<ImageView>(R.id.weatherIconImageView)!! }
    private val weatherDescriptionTextView by lazy { findViewById<TextView>(R.id.weatherDescriptionTextView)!! }
    private val temperatureTextView by lazy { findViewById<TextView>(R.id.temperatureTextView)!! }

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    private var updateJob: Job? = null

    override fun setupLayout() {
        inflate(context, R.layout.weather_widget_card, this)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val lifecycleOwner = findViewTreeLifecycleOwner() ?: return

        updateJob = lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                sharedPreferences.valueFlow(WEATHER_CITY) { weatherCity }.collectLatest { city ->
                    if (city == null) {
                        showMessage(R.string.weather_configure)
                        return@collectLatest
                    }

                    while (true) {
                        val weather = OpenMeteo.currentWeather(city)

                        if (weather != null) {
                            showWeather(weather)
                            delay(REFRESH_INTERVAL_MS)
                        } else {
                            showMessage(R.string.weather_unavailable)
                            delay(RETRY_INTERVAL_MS)
                        }
                    }
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        updateJob?.cancel()

        super.onDetachedFromWindow()
    }

    override fun handleClick() {
        val dialogView = inflate(context, R.layout.weather_widget_options, null)
        val cityEditText = dialogView.findViewById<EditText>(R.id.cityEditText)
        cityEditText.setText(sharedPreferences.weatherCity)

        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.configure_weather_widget)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                sharedPreferences.weatherCity = cityEditText.text.toString().trim().ifEmpty { null }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showWeather(weather: Weather) {
        weatherIconImageView.setImageResource(weather.iconResId)
        weatherDescriptionTextView.setText(weather.descriptionResId)
        temperatureTextView.text = formatTemperature(weather.temperatureCelsius)
    }

    private fun showMessage(messageResId: Int) {
        weatherDescriptionTextView.setText(messageResId)
        temperatureTextView.text = null
    }

    private fun formatTemperature(celsius: Double): String {
        val (formatResId, temperature) = when (LocalePreferences.getTemperatureUnit()) {
            LocalePreferences.TemperatureUnit.FAHRENHEIT ->
                R.string.temperature_f to celsius * 9 / 5 + 32

            LocalePreferences.TemperatureUnit.KELVIN ->
                R.string.temperature_k to celsius + 273.15

            else -> R.string.temperature_c to celsius
        }

        return context.getString(formatResId, temperature.roundToInt().toString())
    }

    companion object {
        private const val REFRESH_INTERVAL_MS = 30 * 60 * 1000L
        private const val RETRY_INTERVAL_MS = 60 * 1000L
    }
}
