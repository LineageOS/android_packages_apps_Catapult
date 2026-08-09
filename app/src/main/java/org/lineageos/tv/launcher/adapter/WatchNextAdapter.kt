/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.adapter

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.tvprovider.media.tv.TvContractCompat
import androidx.tvprovider.media.tv.WatchNextProgram
import org.lineageos.tv.launcher.view.WatchNextCard

class WatchNextAdapter : ListAdapter<WatchNextProgram, WatchNextAdapter.ViewHolder>(DIFF_UTIL) {
    override fun submitList(list: List<WatchNextProgram>?) {
        super.submitList(list?.groupedBySeries())
    }

    override fun submitList(list: List<WatchNextProgram>?, commitCallback: Runnable?) {
        super.submitList(list?.groupedBySeries(), commitCallback)
    }

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

    inner class ViewHolder(private val card: WatchNextCard) : RecyclerView.ViewHolder(card) {
        init {
            card.setOnClickListener {
                val context = card.context

                context.startActivity(card.launchIntent)
            }
        }

        fun bind(watchNextProgram: WatchNextProgram) {
            card.setInfo(watchNextProgram)
        }
    }

    companion object {
        @Suppress("RestrictedApi")
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<WatchNextProgram>() {
            override fun areItemsTheSame(
                oldItem: WatchNextProgram,
                newItem: WatchNextProgram
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: WatchNextProgram,
                newItem: WatchNextProgram
            ) = oldItem.hasAnyUpdatedValues(newItem)
        }

        private fun WatchNextProgram.typePriority(): Int = when (watchNextType) {
            TvContractCompat.WatchNextPrograms.WATCH_NEXT_TYPE_CONTINUE -> 0
            TvContractCompat.WatchNextPrograms.WATCH_NEXT_TYPE_NEXT -> 1
            TvContractCompat.WatchNextPrograms.WATCH_NEXT_TYPE_NEW -> 2
            TvContractCompat.WatchNextPrograms.WATCH_NEXT_TYPE_WATCHLIST -> 3
            else -> 4
        }

        private val watchNextComparator = compareBy<WatchNextProgram>(
            { it.typePriority() },
            { -it.lastEngagementTimeUtcMillis }
        )

        // Group by series after WatchNextProgram.typePriority, so that one series cards stay
        // next to each other. E.g. Series1 continue watching, then Series1 next episode.
        private fun List<WatchNextProgram>.groupedBySeries(): List<WatchNextProgram> =
            sortedWith(watchNextComparator)
                .groupBy { it.seriesKey() }
                .values
                .flatten()

        @SuppressLint("RestrictedApi")
        private fun WatchNextProgram.seriesKey(): String = "$packageName::${title ?: "id-$id"}"
    }
}
