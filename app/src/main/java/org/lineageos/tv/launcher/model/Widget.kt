/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

import android.content.Context
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.view.WeatherWidgetCard
import org.lineageos.tv.launcher.view.WidgetCard

class Widget private constructor(
    val type: Type,
    context: Context,
) : Launchable(
    context.getString(type.labelResId),
    WIDGET_PREFIX + type.id,
    AppCompatResources.getDrawable(context, type.iconResId)!!,
    context,
) {
    class Type(
        val id: String,
        @StringRes val labelResId: Int,
        @DrawableRes val iconResId: Int,
        val createCard: (Context) -> WidgetCard,
    )

    companion object {
        const val WIDGET_PREFIX = "WIDGET_"

        val TYPES = listOf(
            Type("weather", R.string.weather_widget_title, R.drawable.ic_sunny, ::WeatherWidgetCard),
        )

        fun all(context: Context) = TYPES.map { Widget(it, context) }

        fun create(context: Context, widgetId: String) = TYPES.find {
            WIDGET_PREFIX + it.id == widgetId
        }?.let { Widget(it, context) }
    }
}
