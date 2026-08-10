/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.lineageos.tv.launcher.theme.ShapesProvider

fun <T> SharedPreferences.valueFlow(
    key: String,
    valueGetter: SharedPreferences.(key: String) -> T,
) = callbackFlow {
    val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
        changedKey?.takeIf { it == key }?.let {
            trySend(valueGetter(it))
        }
    }

    registerOnSharedPreferenceChangeListener(listener)

    // Emit the latest value
    trySend(valueGetter(key))

    awaitClose {
        unregisterOnSharedPreferenceChangeListener(listener)
    }
}

const val FAVORITE_APPS_KEY = "favorite_apps"

/**
 * The list of apps the user added to favorites.
 */
var SharedPreferences.favoriteApps: List<String>
    get() = getString(FAVORITE_APPS_KEY, null)?.split(",") ?: listOf()
    set(value) = edit {
        putString(FAVORITE_APPS_KEY, value.joinToString(","))
    }

const val KNOWN_CHANNELS_KEY = "known_channels"

/**
 * The list of known channels, used for ordering.
 */
var SharedPreferences.knownChannels: List<Long>
    get() = getString(KNOWN_CHANNELS_KEY, null)?.split(",")?.map {
        it.toLong()
    } ?: listOf()
    set(value) = edit {
        putString(KNOWN_CHANNELS_KEY, value.joinToString(","))
    }

const val HIDDEN_CHANNELS_KEY = "hidden_channels"

/**
 * The list of channels' IDs hidden by the user.
 */
var SharedPreferences.hiddenChannels: Set<Long>
    get() = getStringSet(HIDDEN_CHANNELS_KEY, setOf())?.map {
        it.toLong()
    }?.toSet() ?: setOf()
    set(value) = edit {
        putStringSet(HIDDEN_CHANNELS_KEY, value.map { it.toString() }.toSet())
    }

const val HOME_ROLE_REQUEST_DIALOG_DISMISSED = "home_role_request_dialog_dismissed"

/**
 * Whether the user asked to never show again the home role request dialog.
 */
var SharedPreferences.homeRoleRequestDialogDismissed: Boolean
    get() = getBoolean(HOME_ROLE_REQUEST_DIALOG_DISMISSED, false)
    set(value) = edit {
        putBoolean(HOME_ROLE_REQUEST_DIALOG_DISMISSED, value)
    }

const val ICON_SHAPE_KEY = "icon_shape_model"

/**
 * Active icon shape selection. @see [ShapesProvider].
 */
var SharedPreferences.iconShape: String
    get() = "four_sided_cookie"//getString(ICON_SHAPE_KEY, null) ?: ShapesProvider.CARD_KEY
    set(value) = edit { putString(ICON_SHAPE_KEY, value) }
