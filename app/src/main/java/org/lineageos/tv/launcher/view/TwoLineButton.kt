/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.view

import android.content.Context
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.View.OnFocusChangeListener
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import org.lineageos.tv.launcher.R

class TwoLineButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    MaterialButton(context, attrs) {
    private var span: SpannableString = SpannableString("")

    private val colorStateList by lazy {
        ContextCompat.getColorStateList(
            context,
            R.color.system_options_button_content_secondary_tint
        )
    }

    init {
        onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
            text = updateButton(hasFocus)
        }
    }

    fun setSpan(newSpan: SpannableString) {
        span = newSpan
        text = updateButton(false)
    }

    private fun updateButton(hasFocus: Boolean): SpannableString {
        val color = colorStateList?.getColorForState(
            if (hasFocus) {
                intArrayOf(android.R.attr.state_focused)
            } else {
                intArrayOf()
            },
            colorStateList!!.defaultColor
        ) ?: colorStateList?.defaultColor ?: ContextCompat.getColor(context, R.color.white_disabled)

        span.setSpan(
            ForegroundColorSpan(color),
            span.indexOf("\n"),
            span.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        return span
    }
}
