/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.adapter

import android.view.KeyEvent
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.lineageos.tv.launcher.ext.pixelsEqualTo
import org.lineageos.tv.launcher.model.Launchable
import org.lineageos.tv.launcher.view.Card

abstract class TvAdapter<L : Launchable, C : Card> : ListAdapter<L, TvAdapter<L, C>.ViewHolder>(
    getDiffCallback()
) {
    @CallSuper
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val card: C) : RecyclerView.ViewHolder(card) {
        init {
            card.apply {
                setOnClickListener {
                    handleClick(this)
                }
                setOnLongClickListener {
                    handleLongClick(this)
                }
                setOnKeyListener { _, keyCode, event ->
                    handleKey(this, keyCode, event, bindingAdapterPosition)
                }
            }
        }

        fun bind(item: L) {
            card.setCardInfo(item)
        }
    }

    open fun handleClick(card: C) {
        val context = card.context
        context.startActivity(card.launchIntent)
    }

    open fun handleLongClick(card: C) = false

    open fun handleKey(
        card: C,
        keyCode: Int,
        event: KeyEvent?,
        bindingAdapterPosition: Int,
    ) = false

    companion object {
        private fun <T : Launchable> getDiffCallback() = object : DiffUtil.ItemCallback<T>() {
            override fun areItemsTheSame(
                oldItem: T,
                newItem: T,
            ) = oldItem.packageName == newItem.packageName

            override fun areContentsTheSame(
                oldItem: T,
                newItem: T,
            ) = oldItem.label == newItem.label && oldItem.icon.pixelsEqualTo(newItem.icon)
        }
    }
}
