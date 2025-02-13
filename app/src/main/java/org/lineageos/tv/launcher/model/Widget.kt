/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

import android.content.Context
import android.graphics.drawable.Drawable
import org.lineageos.tv.launcher.view.WeatherWidgetCard

class Widget(
    val id: String,
    label: String,
    icon: Drawable,
    context: Context,
) : Launchable(label, id, icon, context) {
    companion object {
        const val WIDGET_PREFIX = "WIDGET_"
        val WIDGETS = arrayOf(WeatherWidgetCard.WEATHER_WIDGET_CARD_ID)

        fun create(context: Context, widgetId: String): Widget? {
            return if (widgetId.removePrefix(WIDGET_PREFIX) == WeatherWidgetCard.WEATHER_WIDGET_CARD_ID) {
                WeatherWidgetCard.createModel(context)
            } else {
                null
            }
        }
    }
}
