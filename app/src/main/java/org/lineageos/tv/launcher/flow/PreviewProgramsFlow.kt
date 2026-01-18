/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.flow

import android.content.Context
import androidx.core.os.bundleOf
import androidx.tvprovider.media.tv.PreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat
import org.lineageos.tv.launcher.ext.mapEachRow
import org.lineageos.tv.launcher.ext.queryFlow

class PreviewProgramsFlow(
    private val context: Context,
    private val channelId: Long,
) : QueryFlow<PreviewProgram> {
    @Suppress("RestrictedApi")
    override fun flowData() = context.contentResolver.queryFlow(
        TvContractCompat.buildPreviewProgramsUriForChannel(channelId),
        PreviewProgram.PROJECTION,
        bundleOf(),
    ).mapEachRow { it, _ ->
        PreviewProgram.fromCursor(it)
    }
}
