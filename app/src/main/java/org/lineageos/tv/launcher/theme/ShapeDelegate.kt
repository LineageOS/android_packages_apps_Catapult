/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.theme

import android.annotation.SuppressLint
import android.graphics.Matrix
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import androidx.core.graphics.PathParser


/** Abstract representation of the shape of an icon shape */
class ShapeDelegate(private val pathString: String) {
    @SuppressLint("RestrictedApi")
    private val basePath = PathParser.createPathFromPathData(pathString)
    private val pathBounds = RectF().also { basePath.computeBounds(it, true) }
    private val matrix = Matrix()
    private val boundsF = RectF()
    private val path = Path()

    fun getPath(bounds: Rect): Path {
        boundsF.set(bounds)
        matrix.setRectToRect(pathBounds, boundsF, Matrix.ScaleToFit.FILL)
        path.reset()
        basePath.transform(matrix, path)
        return path
    }

    override fun equals(other: Any?) = other is ShapeDelegate && other.pathString == pathString

    override fun hashCode() = pathString.hashCode()
}
