/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.tvprovider.media.tv.PreviewProgram
import org.lineageos.tv.launcher.view.WatchNextCard

class PreviewProgramsAdapter :
    ListAdapter<PreviewProgram, PreviewProgramsAdapter.ViewHolder>(diffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        WatchNextCard(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
            )
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        }
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val watchNextCard: WatchNextCard
    ) : RecyclerView.ViewHolder(watchNextCard) {
        init {
            watchNextCard.setOnClickListener {
                val context = watchNextCard.context

                context.startActivity(watchNextCard.launchIntent)
            }
        }

        fun bind(previewProgram: PreviewProgram) {
            watchNextCard.setInfo(previewProgram)
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PreviewProgram>() {
            @Suppress("RestrictedApi")
            override fun areItemsTheSame(
                oldItem: PreviewProgram,
                newItem: PreviewProgram
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: PreviewProgram,
                newItem: PreviewProgram
            ) = oldItem.hasAnyUpdatedValues(newItem)
        }
    }
}
