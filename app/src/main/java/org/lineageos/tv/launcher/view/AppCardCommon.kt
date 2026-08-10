/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.core.view.isInvisible
import androidx.core.view.updateLayoutParams
import androidx.preference.PreferenceManager
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.ext.iconShape
import org.lineageos.tv.launcher.model.ActivityLauncher
import org.lineageos.tv.launcher.model.Launchable
import org.lineageos.tv.launcher.model.LeanbackAppInfo
import org.lineageos.tv.launcher.theme.ShapesProvider
import org.lineageos.tv.launcher.utils.AppManager
import kotlin.reflect.safeCast

abstract class AppCardCommon @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Card(context, attrs, defStyleAttr) {
    abstract val menuResId: Int

    @get:DimenRes
    abstract val iconSizeRes: Int

    @get:DimenRes
    abstract val shapedIconSizeRes: Int

    // Views
    protected val appGraphicContainer by lazy { findViewById<FrameLayout>(R.id.app_graphic_container)!! }
    private val cardContainer by lazy { findViewById<LinearLayout>(R.id.card_container)!! }
    private val bannerView by lazy { findViewById<ImageView>(R.id.app_banner)!! }
    private val iconContainer by lazy { findViewById<LinearLayout>(R.id.app_with_icon)!! }
    private val iconView by lazy { findViewById<ImageView>(R.id.app_icon)!! }
    protected val nameView by lazy { findViewById<TextView>(R.id.app_name)!! }

    private var uninstallable: Boolean = true

    private val iconSize by lazy { resources.getDimensionPixelSize(iconSizeRes) }
    private val shapedIconSize by lazy { resources.getDimensionPixelSize(shapedIconSizeRes) }
    private val shapedCardPadding by lazy {
        resources.getDimensionPixelSize(R.dimen.shaped_icon_card_padding)
    }
    private val cardWidth by lazy { cardContainer.layoutParams.width }
    private val iconContainerBackground by lazy { iconContainer.background }
    private val focusShadowAnimator by lazy { appGraphicContainer.stateListAnimator }

    init {
        setupNameMarquee()
    }

    private fun setupNameMarquee() {
        setOnFocusChangeListener { _, hasFocus ->
            nameView.isInvisible = !hasFocus
            if (hasFocus) {
                nameView.postDelayed({ nameView.isSelected = true }, 2000)
            } else {
                nameView.isSelected = false
            }
        }
    }

    override fun setCardInfo(appInfo: Launchable) {
        super.setCardInfo(appInfo)

        val shapeModel = ShapesProvider.forKey(
            PreferenceManager.getDefaultSharedPreferences(context).iconShape
        )
        val shaped = shapeModel.shape != null && appInfo !is ActivityLauncher
        val banner = LeanbackAppInfo::class.safeCast(appInfo)?.banner

        // Reset
        bannerView.visibility = GONE
        iconContainer.visibility = VISIBLE
        iconContainer.background = iconContainerBackground
        appGraphicContainer.stateListAnimator = focusShadowAnimator

        nameView.text = appInfo.label

        val size = if (shaped) shapedIconSize else iconSize
        iconView.updateLayoutParams {
            width = size
            height = size
        }
        if (shaped) {
            iconView.setImageDrawable(appInfo.shapedIcon(resources, shapeModel, size))
        } else {
            iconView.setImageDrawable(appInfo.icon)
        }

        // Reduce width if there is only a shaped icon
        val width = if (shaped) size + shapedCardPadding * 2 else cardWidth
        cardContainer.updateLayoutParams<LayoutParams> { this.width = width }
        nameView.updateLayoutParams<LayoutParams> { this.width = width }

        if (appInfo is LeanbackAppInfo) {
            uninstallable = appInfo.isUninstallable()
        }

        if (shaped) {
            // No card, only icon
            iconContainer.background = null
            appGraphicContainer.stateListAnimator = null
            appGraphicContainer.translationZ = 0f
        } else if (banner != null) {
            // Card
            bannerView.setImageDrawable(banner)
            bannerView.visibility = VISIBLE
            iconContainer.visibility = GONE
        }
    }

    fun showPopupMenu() {
        val popupMenu = PopupMenu(context, this, Gravity.START, 0, R.style.PopupMenu)
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
