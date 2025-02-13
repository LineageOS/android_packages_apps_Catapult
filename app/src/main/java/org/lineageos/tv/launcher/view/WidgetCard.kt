/*
 * SPDX-FileCopyrightText: 2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */
package org.lineageos.tv.launcher.view

import android.content.Context
import android.util.AttributeSet
import org.lineageos.tv.launcher.model.Launchable

abstract class WidgetCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FavoriteCard(context, attrs, defStyleAttr) {

    override var uninstallable: Boolean = false

    abstract fun handleClick()

    override fun setCardInfo(appInfo: Launchable) {
        packageName = appInfo.packageName
    }
}
