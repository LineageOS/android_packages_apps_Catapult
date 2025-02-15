/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.content.Context
import android.util.AttributeSet
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.ext.getAttributeResourceId
import org.lineageos.tv.launcher.model.Launchable
import org.lineageos.tv.launcher.model.WidgetInfo

class AddFavoriteWidgetItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AddFavoriteItemView(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.favorites_add_app_card, this)
        setBackgroundResource(
            context.getAttributeResourceId(android.R.attr.selectableItemBackground)
        )
    }

    override fun setCardInfo(appInfo: Launchable) {
        packageName = WidgetInfo.WIDGET_PREFIX + appInfo.packageName
        nameView.text = context.getString(R.string.favorites_add_widget_widget_title, appInfo.label)
        iconView.setImageDrawable(appInfo.icon)
    }
}
