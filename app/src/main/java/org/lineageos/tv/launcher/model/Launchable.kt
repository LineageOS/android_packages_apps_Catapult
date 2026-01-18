/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable

sealed class Launchable(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    val context: Context,
) {
    open val hasMenu = true
    val launchIntent by lazy { setIntent() }

    protected open fun setIntent(): Intent? = null
}
