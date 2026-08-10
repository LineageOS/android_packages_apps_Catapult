/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.drawable.Drawable
import org.lineageos.tv.launcher.ext.asShapedIcon

sealed class Launchable(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    val context: Context,
) {
    open val hasMenu = true
    val launchIntent by lazy { setIntent() }

    protected open fun setIntent(): Intent? = null

    private var _shapedIcon: Triple<IconShapeModel, Int, Drawable>? = null

    /** [icon], masked to [shapeModel]. */
    fun shapedIcon(resources: Resources, shapeModel: IconShapeModel, size: Int) =
        _shapedIcon?.takeIf { it.first == shapeModel && it.second == size }?.third
            ?: icon.asShapedIcon(resources, shapeModel, size).also {
                _shapedIcon = Triple(shapeModel, size, it)
            }
}
