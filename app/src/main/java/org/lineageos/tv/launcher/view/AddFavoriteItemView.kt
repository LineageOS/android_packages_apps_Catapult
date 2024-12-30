/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.materialswitch.MaterialSwitch
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.ext.getAttributeResourceId
import org.lineageos.tv.launcher.model.Launchable

class AddFavoriteItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    var packageName: String = ""

    // Views
    private val stateSwitch by lazy { findViewById<MaterialSwitch>(R.id.state_switch)!! }
    private val iconView by lazy { findViewById<ImageView>(R.id.app_icon)!! }
    private val nameView by lazy { findViewById<TextView>(R.id.app_name)!! }

    init {
        inflate(context, R.layout.favorites_add_app_card, this)
        setBackgroundResource(
            context.getAttributeResourceId(android.R.attr.selectableItemBackground)
        )
    }

    fun setActionToggle(favorite: Boolean) {
        stateSwitch.isChecked = favorite
    }

    fun setCardInfo(appInfo: Launchable) {
        packageName = appInfo.packageName
        nameView.text = appInfo.label
        iconView.setImageDrawable(appInfo.icon)
    }
}
