/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.leanback.widget.HorizontalGridView
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.model.MainRowItem

class MainRowItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    // Views
    private val horizontalGridView by lazy { findViewById<HorizontalGridView>(R.id.horizontal_grid)!! }
    private val titleView by lazy { findViewById<TextView>(R.id.title)!! }

    fun setData(mainRowItem: MainRowItem) {
        titleView.text = mainRowItem.label
        horizontalGridView.adapter = mainRowItem.adapter
    }

    init {
        inflate(context, R.layout.vertical_grid_row, this)
        layoutParams = ViewGroup.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.MATCH_PARENT
        )
        clipChildren = false
        clipToPadding = false
    }
}
