/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.materialswitch.MaterialSwitch
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.ext.getAttributeResourceId
import org.lineageos.tv.launcher.model.Channel

class ToggleChannelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    // Views
    private val titleView by lazy { findViewById<TextView>(R.id.title)!! }
    private val switch by lazy { findViewById<MaterialSwitch>(R.id.state_switch)!! }

    var moving = false
    var channel: Channel? = null

    init {
        inflate(context, R.layout.toggle_channel, this)
        isFocusable = true
        isClickable = true
        setBackgroundResource(
            context.getAttributeResourceId(android.R.attr.selectableItemBackground)
        )
    }

    fun setData(channel: Channel, enabled: Boolean) {
        this.channel = channel
        titleView.text = channel.title
        switch.isChecked = enabled
    }

    fun disableToggle() {
        switch.isEnabled = false
    }

    fun setMoving() {
        if (moving) return
        moving = true
        isSelected = true
    }

    fun setMoveDone() {
        if (!moving) return
        moving = false
        isSelected = false
    }
}
