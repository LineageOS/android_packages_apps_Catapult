/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.withSave
import org.lineageos.tv.launcher.model.IconShapeModel
import kotlin.math.sqrt
import kotlin.reflect.safeCast

fun Drawable.asShapedIcon(
    resources: Resources,
    shapeModel: IconShapeModel,
    size: Int,
): Drawable {
    val adaptiveIcon = this as? AdaptiveIconDrawable ?: AdaptiveIconDrawable(
        Color.WHITE.toDrawable(), wrapIntoSquareInset(LEGACY_ICON_SCALE)
    )

    val shape = shapeModel.shape ?: return adaptiveIcon

    val bounds = Rect(0, 0, size, size)
    adaptiveIcon.bounds = bounds

    return createBitmap(size, size).applyCanvas {
        clipPath(shape.getPath(bounds))
        drawColor(Color.BLACK)
        withSave {
            scale(shapeModel.iconScale, shapeModel.iconScale, size / 2f, size / 2f)
            adaptiveIcon.background?.draw(this)
            adaptiveIcon.foreground?.draw(this)
        }
    }.toDrawable(resources)
}

private val LEGACY_ICON_SCALE =
    .7f * (1f / (1 + 2 * AdaptiveIconDrawable.getExtraInsetFraction()))

private fun Drawable.wrapIntoSquareInset(scale: Float): Drawable {
    val h = intrinsicHeight.toFloat()
    val w = intrinsicWidth.toFloat()
    var scaleX = scale
    var scaleY = scale
    if (h > w && w > 0) {
        scaleX *= w / h
    } else if (w > h && h > 0) {
        scaleY *= h / w
    }
    scaleX = (1 - scaleX) / 2
    scaleY = (1 - scaleY) / 2
    return InsetDrawable(this, scaleX, scaleY, scaleX, scaleY)
}

fun <T : Drawable> T.bytesEqualTo(t: T?) = toBitmap().bytesEqualTo(t?.toBitmap(), true)

fun <T : Drawable> T.pixelsEqualTo(t: T?) = toBitmap().pixelsEqualTo(t?.toBitmap(), true)

fun <T : Drawable> T.toBitmap(): Bitmap {
    BitmapDrawable::class.safeCast(this)?.let {
        return it.bitmap.copy(it.bitmap.config ?: Bitmap.Config.ARGB_8888, true)
    }

    val bitmap = createBitmap(intrinsicWidth, intrinsicHeight)
    val canvas = Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}
