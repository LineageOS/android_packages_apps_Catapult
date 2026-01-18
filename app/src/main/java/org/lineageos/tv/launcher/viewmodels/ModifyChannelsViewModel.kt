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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.lineageos.tv.launcher.ext.context
import org.lineageos.tv.launcher.model.Channel
import org.lineageos.tv.launcher.repository.LauncherRepository
import org.lineageos.tv.launcher.utils.Suggestions
import org.lineageos.tv.launcher.utils.Suggestions.orderSuggestions

class ModifyChannelsViewModel(application: Application) : AndroidViewModel(application) {
    val channelsToEnabled = LauncherRepository.previewChannels(context)
        .map { previewChannels ->
            listOf(
                Channel.getFavoritesAppsChannel(context),
                Channel.getWatchNextChannel(context),
                *previewChannels.map {
                    Channel(
                        it.id,
                        Suggestions.getChannelTitle(context, it),
                        it
                    )
                }.toTypedArray(),
                Channel.getAllAppsChannel(context),
            )
        }
        .combine(LauncherRepository.hiddenChannels(context)) { previewChannels, hiddenChannels ->
            previewChannels.map { it to !hiddenChannels.contains(it.id) }
        }
        .combine(LauncherRepository.knownChannels(context)) { previewChannels, knownChannels ->
            previewChannels.orderSuggestions(knownChannels) { it.first.id }
        }
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf(),
        )
}
