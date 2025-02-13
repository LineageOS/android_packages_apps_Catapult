/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import org.lineageos.tv.launcher.ext.context
import org.lineageos.tv.launcher.model.Widget
import org.lineageos.tv.launcher.repository.LauncherRepository

class AddFavoriteViewModel(application: Application) : AndroidViewModel(application) {
    val appsToFavorites = LauncherRepository.installedApps(context)
        .combine(LauncherRepository.favoriteApps(context)) { installedApps, favoriteApps ->
            val favoriteAppsList = installedApps.map { app ->
                app to (favoriteApps.contains(app.packageName))
            }

            val widgetList = favoriteApps.filter { it.startsWith(Widget.WIDGET_PREFIX) }
            val favoriteWidgetList = Widget.WIDGETS.map { widgetId ->
                Widget.create(context, widgetId)?.let { it to widgetList.contains(it.id) }
            }

            favoriteAppsList + favoriteWidgetList
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf(),
        )
}
