/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.core.view.ancestors
import androidx.leanback.widget.BaseGridView

/**
 * Focus this view when a mouse pointer enters it.
 */
@SuppressLint("RestrictedApi")
fun View.focusOnHover() = setOnHoverListener { view, event ->
    if (event.action == MotionEvent.ACTION_HOVER_ENTER) {
        view.setGridsFocusScrollStrategy(BaseGridView.FOCUS_SCROLL_ITEM)
        view.requestFocusFromTouch()
    }
    false
}

@SuppressLint("RestrictedApi")
fun View.setGridsFocusScrollStrategy(strategy: Int) {
    ancestors.filterIsInstance<BaseGridView>().forEach {
        it.focusScrollStrategy = strategy
    }
}
