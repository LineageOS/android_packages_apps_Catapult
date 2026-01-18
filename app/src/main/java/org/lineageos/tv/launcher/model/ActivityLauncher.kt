/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import com.google.android.material.R
import org.lineageos.tv.launcher.ext.getAttributeColor

class ActivityLauncher(
    label: String,
    icon: Drawable,
    context: Context,
    private val intent: Intent,
) : Launchable(label, "", icon, context) {
    override var hasMenu: Boolean = false

    init {
        icon.setTint(context.getAttributeColor(R.attr.colorOnSecondaryContainer))
    }

    override fun setIntent() = intent
}
