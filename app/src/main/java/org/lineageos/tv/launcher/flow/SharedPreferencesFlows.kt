/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.flow

import android.content.Context
import androidx.preference.PreferenceManager
import org.lineageos.tv.launcher.ext.FAVORITE_APPS_KEY
import org.lineageos.tv.launcher.ext.HIDDEN_CHANNELS_KEY
import org.lineageos.tv.launcher.ext.ICON_SHAPE_KEY
import org.lineageos.tv.launcher.ext.KNOWN_CHANNELS_KEY
import org.lineageos.tv.launcher.ext.favoriteApps
import org.lineageos.tv.launcher.ext.hiddenChannels
import org.lineageos.tv.launcher.ext.iconShape
import org.lineageos.tv.launcher.ext.knownChannels
import org.lineageos.tv.launcher.ext.valueFlow

class SharedPreferencesFlows(context: Context) {
    private val sharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun favoriteAppsFlow() = sharedPreferences.valueFlow(FAVORITE_APPS_KEY) { favoriteApps }

    fun knownChannelsFlow() = sharedPreferences.valueFlow(KNOWN_CHANNELS_KEY) { knownChannels }

    fun hiddenChannelsFlow() = sharedPreferences.valueFlow(HIDDEN_CHANNELS_KEY) { hiddenChannels }

    fun iconShapeFlow() = sharedPreferences.valueFlow(ICON_SHAPE_KEY) { iconShape }
}
