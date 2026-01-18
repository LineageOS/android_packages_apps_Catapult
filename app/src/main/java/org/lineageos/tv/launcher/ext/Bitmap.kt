/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

fun Bitmap.bytesEqualTo(
    otherBitmap: Bitmap?,
    shouldRecycle: Boolean = false,
) = otherBitmap?.let { other ->
    if (width == other.width && height == other.height) {
        val res = toBytes().contentEquals(other.toBytes())
        if (shouldRecycle) {
            doRecycle().also { other.doRecycle() }
        }
        res
    } else false
} ?: false

fun Bitmap.pixelsEqualTo(
    otherBitmap: Bitmap?,
    shouldRecycle: Boolean = false,
) = otherBitmap?.let { other ->
    if (width == other.width && height == other.height) {
        val res = toPixels().contentEquals(other.toPixels())
        if (shouldRecycle) {
            doRecycle().also { other.doRecycle() }
        }
        res
    } else false
} ?: false

fun Bitmap.doRecycle() {
    if (!isRecycled) recycle()
}

fun Bitmap.toBytes(): ByteArray = ByteArrayOutputStream().use { stream ->
    compress(Bitmap.CompressFormat.JPEG, 100, stream)
    stream.toByteArray()
}

fun Bitmap.toPixels() = IntArray(width * height).apply {
    getPixels(this, 0, width, 0, 0, width, height)
}
