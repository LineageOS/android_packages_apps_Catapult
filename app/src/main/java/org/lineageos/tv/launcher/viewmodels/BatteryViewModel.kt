/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import org.lineageos.tv.launcher.ext.batteryStatusFlow
import org.lineageos.tv.launcher.model.BatteryInfo

class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()

    val batteryState: StateFlow<BatteryInfo?> = context.batteryStatusFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = null
        )
}
