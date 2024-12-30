/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.lineageos.tv.launcher.ext.pixelsEqualTo
import org.lineageos.tv.launcher.model.LeanbackAppInfo
import org.lineageos.tv.launcher.view.AddFavoriteItemView

class ModifyFavoritesAdapter :
    ListAdapter<Pair<LeanbackAppInfo, Boolean>, ModifyFavoritesAdapter.ViewHolder>(diffCallback) {
    var onFavoriteChanged: (packageName: String, favorite: Boolean) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        AddFavoriteItemView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val card: AddFavoriteItemView) : RecyclerView.ViewHolder(card) {
        init {
            card.apply {
                setOnClickListener {
                    val packageName = card.packageName

                    currentList.find {
                        it.first.packageName == card.packageName
                    }?.let {
                        onFavoriteChanged(packageName, !it.second)
                    }
                }
            }
        }

        fun bind(item: Pair<LeanbackAppInfo, Boolean>) {
            card.setCardInfo(item.first)
            card.setActionToggle(item.second)
        }
    }

    companion object {
        private val diffCallback =
            object : DiffUtil.ItemCallback<Pair<LeanbackAppInfo, Boolean>>() {
            override fun areItemsTheSame(
                oldItem: Pair<LeanbackAppInfo, Boolean>,
                newItem: Pair<LeanbackAppInfo, Boolean>
            ) = oldItem.first.packageName == newItem.first.packageName

            override fun areContentsTheSame(
                oldItem: Pair<LeanbackAppInfo, Boolean>,
                newItem: Pair<LeanbackAppInfo, Boolean>
            ) = compareValuesBy(
                oldItem, newItem,
                { it.first.label },
                { it.second },
            ) == 0 && oldItem.first.icon.pixelsEqualTo(newItem.first.icon)
        }
    }
}
