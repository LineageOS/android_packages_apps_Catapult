/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.animation.AnimatorInflater
import android.content.Context
import android.content.Intent
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import android.widget.LinearLayout
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.model.Launchable

abstract class Card @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    // View common across all cards
    private val appGraphicContainer: FrameLayout by lazy { findViewById(R.id.app_graphic_container)!! }

    var packageName: String = ""
    var label: String = ""
    var launchIntent: Intent? = null
    var hasMenu: Boolean = true

    init {
        clipChildren = false
    }

    open fun setCardInfo(appInfo: Launchable) {
        label = appInfo.label
        packageName = appInfo.packageName
        launchIntent = appInfo.launchIntent
        hasMenu = appInfo.hasMenu
    }

    private fun applyRoundedOutline(cornerRadius: Float, view: View = this) {
        view.outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
            }
        }
    }

    protected fun setupFocusShadow(cornerRadius: Float) {
        applyRoundedOutline(cornerRadius, appGraphicContainer)
        appGraphicContainer.stateListAnimator =
            AnimatorInflater.loadStateListAnimator(
                context,
                R.animator.card_banner_shadow_state_animator
            )
    }
}
