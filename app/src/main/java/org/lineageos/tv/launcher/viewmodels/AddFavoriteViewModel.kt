/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
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
import org.lineageos.tv.launcher.model.WidgetInfo
import org.lineageos.tv.launcher.repository.LauncherRepository

class AddFavoriteViewModel(application: Application) : AndroidViewModel(application) {
    private val appsToFavorites = LauncherRepository.installedApps(context)
        .combine(LauncherRepository.favorites(context)) { installedApps, favoriteApps ->
            installedApps.map { it to favoriteApps.contains(it.packageName) }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf(),
        )

    private val widgetsToFavorites = LauncherRepository.installedWidgets(context)
        .combine(LauncherRepository.favorites(context)) { installedWidgets, favorites ->
            installedWidgets.map { widget ->
                widget to favorites.contains(WidgetInfo.WIDGET_PREFIX + widget.packageName)
            }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )

    val favorites = combine(appsToFavorites, widgetsToFavorites) { apps, widgets ->
        apps + widgets
    }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = emptyList()
        )
}
