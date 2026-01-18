/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.flow

import android.content.Context
import androidx.core.os.bundleOf
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.WatchNextProgram
import org.lineageos.tv.launcher.ext.mapEachRow
import org.lineageos.tv.launcher.ext.queryFlow

@Suppress("RestrictedApi")
class WatchNextProgramsFlow(private val context: Context) : QueryFlow<WatchNextProgram> {
    override fun flowData() = context.contentResolver.queryFlow(
        TvContractCompat.WatchNextPrograms.CONTENT_URI,
        WatchNextProgram.PROJECTION,
        bundleOf(),
    ).mapEachRow { it, _ ->
        WatchNextProgram.fromCursor(it)
    }
}
