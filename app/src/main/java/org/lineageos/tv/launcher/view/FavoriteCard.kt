/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.content.Context
import android.util.AttributeSet
import org.lineageos.tv.launcher.R

open class FavoriteCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FavoriteRowCard(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.favorites_app_card, this)
    }
}
