/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

import androidx.annotation.StringRes
import org.lineageos.tv.launcher.theme.ShapeDelegate

data class IconShapeModel(
    val key: String,
    @StringRes val titleId: Int,
    val pathString: String?,
    val iconScale: Float = 1f,
) {
    val shape by lazy { pathString?.let { ShapeDelegate(it) } }
}
