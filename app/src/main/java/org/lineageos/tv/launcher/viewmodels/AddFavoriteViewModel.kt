/*
 * SPDX-FileCopyrightText: The LineageOS Project
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
import org.lineageos.tv.launcher.repository.LauncherRepository

class AddFavoriteViewModel(application: Application) : AndroidViewModel(application) {
    val appsToFavorites = LauncherRepository.installedApps(context)
        .combine(LauncherRepository.favoriteApps(context)) { installedApps, favoriteApps ->
            installedApps.map { it to favoriteApps.contains(it.packageName) }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf(),
        )
}
