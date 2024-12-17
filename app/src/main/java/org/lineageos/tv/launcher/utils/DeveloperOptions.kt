/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.utils

import android.content.Context

import org.lineageos.internal.tv.AdbNetworkManager

class DeveloperOptions(val context: Context) {

    private val adbNetworkManager: AdbNetworkManager = AdbNetworkManager.getInstance(context)

    fun adbOverNetworkEnabled(): Boolean {
        return adbNetworkManager.getEnabled()
    }

    fun toggleAdbOverNetwork() {
        adbNetworkManager.setEnabled(!adbOverNetworkEnabled())
    }
}