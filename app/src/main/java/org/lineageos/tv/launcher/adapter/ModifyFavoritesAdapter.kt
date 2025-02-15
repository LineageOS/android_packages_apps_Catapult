/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.lineageos.tv.launcher.ext.pixelsEqualTo
import org.lineageos.tv.launcher.model.Launchable
import org.lineageos.tv.launcher.model.WidgetInfo
import org.lineageos.tv.launcher.view.AddFavoriteItemView
import org.lineageos.tv.launcher.view.AddFavoriteWidgetItemView

class ModifyFavoritesAdapter :
    ListAdapter<Pair<Launchable, Boolean>, ModifyFavoritesAdapter.ViewHolder>(diffCallback) {
    var onFavoriteChanged: (packageName: String, favorite: Boolean) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            VIEW_TYPE_APP_CARD ->
                ViewHolder(
                    AddFavoriteItemView(parent.context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                )

            VIEW_TYPE_WIDGET_CARD ->
                ViewHolder(
                    AddFavoriteWidgetItemView(parent.context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    })

            else -> throw IllegalArgumentException("Unknown view type: $viewType")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val card: AddFavoriteItemView) : RecyclerView.ViewHolder(card) {
        init {
            card.apply {
                setOnClickListener {
                    val packageName = card.packageName

                    currentList.find {
                        (it.first.packageName == packageName && it.first !is WidgetInfo) ||
                                (WidgetInfo.WIDGET_PREFIX + it.first.packageName == packageName && it.first is WidgetInfo)
                    }?.let {
                        onFavoriteChanged(packageName, !it.second)
                    }
                }
            }
        }

        fun bind(item: Pair<Launchable, Boolean>) {
            card.setCardInfo(item.first)
            card.setActionToggle(item.second)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (currentList[position].first is WidgetInfo) {
            VIEW_TYPE_WIDGET_CARD
        } else {
            VIEW_TYPE_APP_CARD
        }
    }

    companion object {
        private const val VIEW_TYPE_APP_CARD = 1
        private const val VIEW_TYPE_WIDGET_CARD = 2

        private val diffCallback =
            object : DiffUtil.ItemCallback<Pair<Launchable, Boolean>>() {
                override fun areItemsTheSame(
                    oldItem: Pair<Launchable, Boolean>,
                    newItem: Pair<Launchable, Boolean>
                ) = oldItem.first.packageName == newItem.first.packageName

                override fun areContentsTheSame(
                    oldItem: Pair<Launchable, Boolean>,
                    newItem: Pair<Launchable, Boolean>
                ) = compareValuesBy(
                    oldItem, newItem,
                    { it.first.label },
                    { it.second },
                ) == 0 && oldItem.first.icon.pixelsEqualTo(newItem.first.icon)
            }
    }
}
