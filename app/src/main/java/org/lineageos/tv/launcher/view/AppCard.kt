/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import org.lineageos.tv.launcher.R

class AppCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCardCommon(context, attrs, defStyleAttr) {
    override val menuResId = R.menu.app_long_press

    init {
        inflate(context, R.layout.app_card, this)

        stateListAnimator =
            AnimatorInflater.loadStateListAnimator(context, R.animator.app_card_state_animator)

        setOnFocusChangeListener { _, hasFocus ->
            nameView.isInvisible = !hasFocus
            if (hasFocus) {
                nameView.postDelayed({ nameView.isSelected = true }, 2000)
            } else {
                nameView.isSelected = false
            }
        }
    }
}
