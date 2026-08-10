/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import androidx.core.view.isVisible
import org.lineageos.tv.launcher.R

class FavoriteCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCardCommon(context, attrs, defStyleAttr) {
    override val menuResId = R.menu.favorite_app_long_press

    // Views
    val moveLeftImageButton by lazy { findViewById<ImageView>(R.id.app_move_left)!! }
    val moveRightImageButton by lazy { findViewById<ImageView>(R.id.app_move_right)!! }

    var moving: Boolean = false

    init {
        inflate(context, R.layout.favorites_app_card, this)

        setupFocusShadow(resources.getDimension(R.dimen.favorite_card_radius))

        stateListAnimator =
            AnimatorInflater.loadStateListAnimator(context, R.animator.app_card_state_animator)
    }

    fun setMoving() {
        moveLeftImageButton.isVisible = true
        moveRightImageButton.isVisible = true
        translationZ = resources.getDimension(R.dimen.card_focus_elevation)
        moving = true
    }

    fun setMoveDone() {
        moveLeftImageButton.isVisible = false
        moveRightImageButton.isVisible = false
        translationZ = 0f
        moving = false
    }
}
