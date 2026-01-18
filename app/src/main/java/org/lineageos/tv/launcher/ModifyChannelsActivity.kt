/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher

import android.os.Bundle
import android.widget.ProgressBar
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.preference.PreferenceManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.lineageos.tv.launcher.adapter.ModifyChannelsAdapter
import org.lineageos.tv.launcher.ext.knownChannels
import org.lineageos.tv.launcher.utils.PermissionsGatedCallback
import org.lineageos.tv.launcher.utils.Suggestions
import org.lineageos.tv.launcher.viewmodels.ModifyChannelsViewModel

class ModifyChannelsActivity : ModalActivity(R.layout.activity_modify_channels) {
    // View models
    private val model: ModifyChannelsViewModel by viewModels()

    // Views
    private val channelsGrid by lazy { findViewById<VerticalGridView>(R.id.modify_channels_grid)!! }
    private val progressLoadingChannels by lazy { findViewById<ProgressBar>(R.id.progress_loading_channels)!! }

    // Adapters
    private val modifyChannelsAdapter by lazy { ModifyChannelsAdapter() }

    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(this)!!
    }

    private val permissionsGatedCallback = PermissionsGatedCallback(this) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.channelsToEnabled.collectLatest {
                    modifyChannelsAdapter.submitList(it.toList())

                    // Display the data & hide spinner
                    channelsGrid.isVisible = true
                    progressLoadingChannels.isVisible = false
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        modifyChannelsAdapter.onChannelToggled = { channel, enabled ->
            Suggestions.toggleChannel(this, channel.id, enabled)
        }
        modifyChannelsAdapter.onOrderChanged = {
            sharedPreferences.knownChannels = it.map { channel -> channel.id }
        }

        channelsGrid.adapter = modifyChannelsAdapter

        permissionsGatedCallback.runAfterPermissionsCheck()
    }
}
