/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import org.lineageos.tv.launcher.R

class FavoriteCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCardCommon(context, attrs, defStyleAttr) {
    override val menuResId = R.menu.favorite_app_long_press

    // Views
    private val moveOverlayView by lazy { findViewById<ImageView>(R.id.app_move_handle)!! }

    var moving: Boolean = false

    init {
        inflate(context, R.layout.favorites_app_card, this)

        stateListAnimator =
            AnimatorInflater.loadStateListAnimator(context, R.animator.app_card_state_animator)
    }

    fun setMoving() {
        moveOverlayView.isVisible = true
        moving = true
    }

    fun setMoveDone() {
        moveOverlayView.isVisible = false
        moving = false
    }
}
