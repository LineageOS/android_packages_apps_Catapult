/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import android.annotation.SuppressLint
import android.content.Context
import androidx.tvprovider.media.tv.BasePreviewProgram
import org.lineageos.tv.launcher.R

@SuppressLint("RestrictedApi")
fun BasePreviewProgram.displayTitle(context: Context): String {
    val season = seasonNumber
    val episode = episodeNumber

    if (season.isNullOrBlank() && episode.isNullOrBlank()) {
        return title.orEmpty()
    }

    val tag = buildString {
        if (!season.isNullOrBlank()) {
            append(context.getString(R.string.program_season, season))
        }
        if (!episode.isNullOrBlank()) {
            append(context.getString(R.string.program_episode, episode))
        }
    }

    return context.getString(
        R.string.program_title_with_episode_info,
        title.orEmpty(),
        tag
    )
}
