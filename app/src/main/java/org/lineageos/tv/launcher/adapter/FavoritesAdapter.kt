/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.adapter

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import org.lineageos.tv.launcher.AddFavoriteActivity
import org.lineageos.tv.launcher.ModifyChannelsActivity
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.model.ActivityLauncher
import org.lineageos.tv.launcher.model.Launchable
import org.lineageos.tv.launcher.model.Widget
import org.lineageos.tv.launcher.view.Card
import org.lineageos.tv.launcher.view.FavoriteCard
import org.lineageos.tv.launcher.view.WeatherWidgetCard
import java.util.Collections

class FavoritesAdapter : TvAdapter<Launchable, Card>() {
    var onFavoritesChangedCallback: (favorites: List<String>) -> Unit = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_FAVORITE_CARD -> {
                ViewHolder(
                    FavoriteCard(parent.context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                    }
                )
            }

            VIEW_TYPE_WIDGET_CARD -> {
                ViewHolder(
                    WeatherWidgetCard(parent.context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                    }
                )
            }

            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun handleKey(
        card: Card,
        keyCode: Int,
        event: KeyEvent?,
        bindingAdapterPosition: Int,
    ): Boolean {
        if (card !is FavoriteCard) return false

        // Leave center key for onClick handler
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            return false
        }

        if (event == null) {
            return false
        }

        // Only handle keyDown events here
        if (event.action != KeyEvent.ACTION_DOWN) {
            return card.moving
        }

        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                if (card.moving) {
                    card.setMoveDone()
                    return true
                }
                return false
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (card.moving) {
                    if (bindingAdapterPosition == 0) {
                        return true
                    }

                    submitList(
                        currentList.toMutableList().apply {
                            Collections.swap(
                                this, bindingAdapterPosition, bindingAdapterPosition - 1
                            )
                        }
                    )

                    return true
                }
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (card.moving) {
                    if (bindingAdapterPosition == currentList.size - 1 - STATIC_ITEMS) {
                        return true
                    }

                    submitList(
                        currentList.toMutableList().apply {
                            Collections.swap(
                                this, bindingAdapterPosition, bindingAdapterPosition + 1
                            )
                        }
                    )

                    return true
                }
            }
            // Don't allow moving up or down while moving a favorite app
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_UP -> return card.moving
        }

        return false
    }

    override fun handleLongClick(card: Card): Boolean {
        if (card !is FavoriteCard) return false

        if (card.moving || !card.hasMenu) {
            return true
        }

        card.showPopupMenu()
        return true
    }

    override fun handleClick(card: Card) {
        if (card !is FavoriteCard) return

        if (!card.moving) {
            super.handleClick(card)
            return
        }

        card.setMoveDone()

        // Save new favorites order
        val newFavoritesSet = mutableListOf<String>()
        for (a in currentList) {
            if (a.packageName != "") {
                newFavoritesSet.add(a.packageName)
            }
        }

        onFavoritesChangedCallback(newFavoritesSet)
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position] is Widget) {
            VIEW_TYPE_WIDGET_CARD
        } else {
            VIEW_TYPE_FAVORITE_CARD
        }
    }

    companion object {
        private const val VIEW_TYPE_FAVORITE_CARD = 1
        private const val VIEW_TYPE_WIDGET_CARD = 2
        const val STATIC_ITEMS = 2

        fun createAddFavoriteEntry(context: Context): Launchable {
            return ActivityLauncher(
                context.getString(R.string.modify_favorites_short),
                AppCompatResources.getDrawable(context, R.drawable.ic_add)!!,
                context,
                Intent(context, AddFavoriteActivity::class.java)
            )
        }

        fun createModifyChannelsEntry(context: Context): Launchable {
            return ActivityLauncher(
                context.getString(R.string.modify_channels),
                AppCompatResources.getDrawable(context, R.drawable.ic_settings)!!,
                context,
                Intent(context, ModifyChannelsActivity::class.java)
            )
        }

        fun createWeatherWidgetEntry(context: Context): Widget {
            return Widget(AppCompatResources.getDrawable(context, R.drawable.ic_sunny)!!, context)
        }
    }
}
