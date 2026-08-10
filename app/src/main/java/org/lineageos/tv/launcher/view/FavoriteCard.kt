/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.core.view.isVisible
import org.lineageos.tv.launcher.R

class FavoriteCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCardCommon(context, attrs, defStyleAttr) {
    override val menuResId = R.menu.favorite_app_long_press
    override val iconSizeRes = R.dimen.favorite_icon_size
    override val shapedIconSizeRes = R.dimen.favorite_icon_size_shaped

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
        animateHandleIn(moveLeftImageButton)
        animateHandleIn(moveRightImageButton)
        translationZ = resources.getDimension(R.dimen.card_focus_elevation)
        moving = true
    }

    fun setMoveDone() {
        animateHandleOut(moveLeftImageButton)
        animateHandleOut(moveRightImageButton)
        translationZ = 0f
        moving = false
    }

    private fun animateHandleIn(view: View) {
        view.animate().cancel()
        view.apply {
            alpha = 0f
            scaleX = 0.7f
            scaleY = 0.7f
            isVisible = true
            animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(150)
                .setInterpolator(AnimationUtils.loadInterpolator(context, R.anim.control_state))
                .start()
        }
    }

    private fun animateHandleOut(view: View) {
        view.animate().cancel()
        view.animate()
            .alpha(0f)
            .scaleX(0.7f)
            .scaleY(0.7f)
            .setDuration(100)
            .setInterpolator(AnimationUtils.loadInterpolator(context, R.anim.control_state))
            .withEndAction { view.isVisible = false }
            .start()
    }
}
