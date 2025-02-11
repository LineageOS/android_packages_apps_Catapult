/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.view

import android.animation.AnimatorInflater
import android.content.Context
import android.text.InputType
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.preference.PreferenceManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.ext.weatherApiKey
import org.lineageos.tv.launcher.ext.weatherCity
import org.lineageos.tv.launcher.viewmodels.WeatherViewModel
import org.lineageos.tv.launcher.weather.WeatherUtils
import org.lineageos.tv.launcher.weather.models.RequestStatus
import kotlin.math.roundToInt

class WeatherWidgetCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Card(context, attrs, defStyleAttr) {

    // Views
    private val weatherIconImageView by lazy { findViewById<ImageView>(R.id.weatherIconImageView)!! }
    private val weatherDescriptionTextView by lazy { findViewById<TextView>(R.id.weatherDescriptionTextView)!! }
    private val temperatureTextView by lazy { findViewById<TextView>(R.id.temperatureTextView)!! }

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    private val weatherViewModel: WeatherViewModel = WeatherViewModel(sharedPreferences)

    init {
        inflate(context, R.layout.weather_widget_card, this)

        stateListAnimator =
            AnimatorInflater.loadStateListAnimator(context, R.animator.app_card_state_animator)

        weatherViewModel.fetchWeather()

        CoroutineScope(Dispatchers.Main).launch {
            weatherViewModel.weatherStatus.collect { status ->
                when (status) {
                    is RequestStatus.Loading -> {
                        weatherDescriptionTextView.text = context.getString(R.string.loading)
                    }

                    is RequestStatus.Success -> {
                        weatherIconImageView.setImageDrawable(
                            AppCompatResources.getDrawable(
                                context,
                                WeatherUtils.getWeatherResId(status.data.weather[0].id)
                            )
                        )
                        weatherDescriptionTextView.text = status.data.weather[0].main
                        temperatureTextView.text = context.getString(
                            R.string.temperature_c,
                            status.data.main.temp.roundToInt().toString()
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

    sealed class ApiKeyDialogResult {
        data class Success(val apiKey: String, val city: String) : ApiKeyDialogResult()
        data object Cancelled : ApiKeyDialogResult()
    }

    fun showDialog() {
        val context = this.context
        context.showApiKeyDialog { result ->
            when (result) {
                is ApiKeyDialogResult.Success -> {
                    weatherViewModel.setCredentials(result.apiKey, result.city)
                }

                is ApiKeyDialogResult.Cancelled -> {
                }
            }
        }
    }

    private fun Context.showApiKeyDialog(onDismiss: (ApiKeyDialogResult) -> Unit) {
        val dialogView = LinearLayout(this).apply {
            orientation = VERTICAL
            setPadding(32, 16, 32, 16)

            addView(createTextInputLayout(getString(R.string.api_key)).apply {
                id = R.id.til_api_key
                editText?.apply {
                    inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
                    setText(sharedPreferences.weatherApiKey)
                }
            })

            addView(createTextInputLayout(getString(R.string.city)).apply {
                id = R.id.til_city
                editText?.apply {
                    inputType = InputType.TYPE_CLASS_TEXT
                    setText(sharedPreferences.weatherCity)
                }
            })
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(getString(R.string.configure_weather_widget))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                val apiKey =
                    dialogView.findViewById<TextInputLayout>(R.id.til_api_key)?.editText?.text?.toString()
                val city =
                    dialogView.findViewById<TextInputLayout>(R.id.til_city)?.editText?.text?.toString()

                when {
                    apiKey.isNullOrEmpty() -> showError(
                        R.id.til_api_key,
                        getString(R.string.api_key_required)
                    )

                    city.isNullOrEmpty() -> showError(
                        R.id.til_city,
                        getString(R.string.city_required)
                    )

                    else -> onDismiss(ApiKeyDialogResult.Success(apiKey, city))
                }
            }
            .setNegativeButton(getString(android.R.string.cancel)) { _, _ ->
                onDismiss(ApiKeyDialogResult.Cancelled)
            }
            .show()
    }

    private fun Context.createTextInputLayout(hint: String): TextInputLayout {
        return TextInputLayout(this).apply {
            layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
            )

            boxBackgroundMode = TextInputLayout.BOX_BACKGROUND_OUTLINE
            this.hint = hint
            addView(TextInputEditText(context))
        }
    }

    private fun showError(viewId: Int, message: String) {
        findViewById<TextInputLayout>(viewId)?.error = message
    }
}