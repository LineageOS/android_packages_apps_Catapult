/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.content.Context
import android.content.Intent
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.LinearLayout
import org.lineageos.tv.launcher.model.Launchable

abstract class Card @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var packageName: String = ""
    var label: String = ""
    var launchIntent: Intent? = null
    var hasMenu: Boolean = true

    open fun setCardInfo(appInfo: Launchable) {
        label = appInfo.label
        packageName = appInfo.packageName
        launchIntent = appInfo.launchIntent
        hasMenu = appInfo.hasMenu
    }

    protected fun applyRoundedOutline(cornerRadius: Float) {
        outlineProvider = object : ViewOutlineProvider() {
            override fun getOutline(view: View, outline: Outline) {
                outline.setRoundRect(0, 0, view.width, view.height, cornerRadius)
            }
        }
    }
}
