/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.adapter

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.model.MainRowItem
import org.lineageos.tv.launcher.view.MainRowItemView

class MainVerticalAdapter :
    ListAdapter<Pair<Long, MainRowItem>, MainVerticalAdapter.ViewHolder>(diffCallback) {
    inner class ViewHolder(
        private val mainRowItemView: MainRowItemView,
    ) : RecyclerView.ViewHolder(mainRowItemView) {
        fun bind(item: Pair<Long, MainRowItem>, position: Int) {
            mainRowItemView.setData(item.second)
            mainRowItemView.updateLayoutParams {
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = mainRowItemView.resources.getDimension(
                    if (item.second.adapter is AllAppsAdapter) {
                        R.dimen.main_all_apps_row_height
                    } else if (item.second.adapter is TvAdapter<*, *>) {
                        R.dimen.main_app_row_height
                    } else {
                        R.dimen.main_row_height
                    }
                ).toInt()
            }

            if (position == 0) {
                mainRowItemView.requestFocus()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        MainRowItemView(parent.context)
    )

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Pair<Long, MainRowItem>>() {
            override fun areItemsTheSame(
                oldItem: Pair<Long, MainRowItem>,
                newItem: Pair<Long, MainRowItem>
            ) = oldItem.first == newItem.first

            @Suppress("DiffUtilEquals")
            override fun areContentsTheSame(
                oldItem: Pair<Long, MainRowItem>,
                newItem: Pair<Long, MainRowItem>
            ) = oldItem.second.label == newItem.second.label
                    && oldItem.second.adapter === newItem.second.adapter
        }
    }
}
