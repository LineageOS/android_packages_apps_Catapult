/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import android.content.Context
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt

private fun Context.resolveAttribute(@AttrRes attribute: Int) = TypedValue().also {
    theme.resolveAttribute(attribute, it, true)
}

fun Context.getAttributeResourceId(@AttrRes attribute: Int) = resolveAttribute(attribute).resourceId

@ColorInt
fun Context.getAttributeColor(@AttrRes attribute: Int) = resolveAttribute(attribute).let {
    require(it.isColorType)
    it.data
}
