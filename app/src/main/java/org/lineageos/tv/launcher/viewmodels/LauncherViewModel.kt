/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.tvprovider.media.tv.PreviewProgram
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.lineageos.tv.launcher.ext.context
import org.lineageos.tv.launcher.model.Channel
import org.lineageos.tv.launcher.repository.LauncherRepository
import org.lineageos.tv.launcher.utils.Suggestions
import org.lineageos.tv.launcher.utils.Suggestions.orderSuggestions
import java.util.Collections

class LauncherViewModel(application: Application) : AndroidViewModel(application) {
    @OptIn(ExperimentalCoroutinesApi::class)
    val channelsToPrograms = LauncherRepository.previewChannels(context)
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
            previewChannels.filter { !hiddenChannels.contains(it.id) }
        }
        .flatMapLatest {
            channelFlow {
                val previewPrograms = Collections.synchronizedMap(
                    mutableMapOf<Long, List<PreviewProgram>?>()
                )

                // Emit a value before launching preview program flows
                send(it.map { it to previewPrograms[it.id] })

                it.filter { channel ->
                    channel.previewChannel != null
                }.forEach { channel ->
                    launch {
                        getPreviewPrograms(channel.id).collect { emittedElement ->
                            previewPrograms[channel.id] = emittedElement
                            send(it.map { it to previewPrograms[it.id] })
                        }
                    }
                }
            }
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

    val favorites = LauncherRepository.favorites(context)
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf(),
        )

    val watchNextPrograms = LauncherRepository.watchNextPrograms(context)
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf(),
        )

    val installedApps = LauncherRepository.installedApps(context)
        .flowOn(Dispatchers.IO)
        .stateIn(
            viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = listOf(),
        )

    private fun getPreviewPrograms(channelId: Long) =
        LauncherRepository.previewPrograms(context, channelId)
            .flowOn(Dispatchers.IO)
            .stateIn(
                viewModelScope,
                started = SharingStarted.WhileSubscribed(),
                initialValue = listOf(),
            )
}
