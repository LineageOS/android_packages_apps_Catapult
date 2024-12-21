/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.model.AppInfo
import org.lineageos.tv.launcher.model.Launchable
import org.lineageos.tv.launcher.utils.AppManager
import kotlin.reflect.safeCast

abstract class AppCardCommon @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Card(context, attrs, defStyleAttr) {
    abstract val menuResId: Int

    // Views
    private val bannerView by lazy { findViewById<ImageView>(R.id.app_banner)!! }
    private val cardContainer by lazy { findViewById<FrameLayout>(R.id.card_container)!! }
    private val iconContainer by lazy { findViewById<LinearLayout>(R.id.app_with_icon)!! }
    private val iconView by lazy { findViewById<ImageView>(R.id.app_icon)!! }
    private val nameView by lazy { findViewById<TextView>(R.id.app_name)!! }

    private var uninstallable: Boolean = true

    override fun setCardInfo(appInfo: Launchable) {
        super.setCardInfo(appInfo)

        nameView.text = appInfo.label
        iconView.setImageDrawable(appInfo.icon)

        if (appInfo is AppInfo) {
            uninstallable = appInfo.isUninstallable()

            if (appInfo.banner != null) {
                // App with a banner
                bannerView.setImageDrawable(appInfo.banner)
                bannerView.visibility = View.VISIBLE
                iconContainer.visibility = View.GONE
                cardContainer.background =
                    AppCompatResources.getDrawable(context, R.drawable.card_border_only)
            }
        } else {
            // App with an icon
            iconView.setImageDrawable(appInfo.icon)
        }
    }

    fun showPopupMenu() {
        val popupMenu = PopupMenu(context, this)
        popupMenu.menuInflater.inflate(menuResId, popupMenu.menu)
        popupMenu.setForceShowIcon(true)

        // See if this card is already a favorite
        if (packageName in AppManager.getFavoriteApps(context)) {
            popupMenu.menu.removeItem(R.id.menu_mark_as_favorite)
        } else {
            popupMenu.menu.removeItem(R.id.menu_remove_favorite)
        }

        // Disable uninstall on certain packages
        if (!uninstallable) {
            popupMenu.menu.removeItem(R.id.menu_uninstall)
        }

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_uninstall -> {
                    AppManager.uninstallApp(context, packageName)
                    true
                }

                R.id.menu_mark_as_favorite -> {
                    AppManager.toggleFavoriteApp(context, packageName, true)
                    true
                }

                R.id.menu_remove_favorite -> {
                    AppManager.toggleFavoriteApp(context, packageName, false)
                    true
                }

                R.id.menu_move -> {
                    FavoriteCard::class.safeCast(this)?.setMoving()
                    true
                }

                else -> false
            }
        }

        popupMenu.show()
    }
}
