/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.adapter

import android.graphics.Rect
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.materialswitch.MaterialSwitch
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.model.Channel
import org.lineageos.tv.launcher.model.InternalChannel
import org.lineageos.tv.launcher.view.ToggleChannelView
import java.util.Collections

class ModifyChannelsAdapter :
    ListAdapter<Pair<Channel, Boolean>, ModifyChannelsAdapter.ViewHolder>(diffCallback) {
    var onChannelToggled: (channel: Channel, enabled: Boolean) -> Unit = { _, _ -> }
    var onOrderChanged: (channels: List<Channel>) -> Unit = {}

    inner class ViewHolder(
        private val toggleChannelView: ToggleChannelView,
    ) : RecyclerView.ViewHolder(toggleChannelView) {

        val switch = itemView.findViewById<MaterialSwitch>(R.id.state_switch)!!

        init {
            toggleChannelView.setOnClickListener {
                if (toggleChannelView.moving) {
                    toggleChannelView.setMoveDone()
                    return@setOnClickListener
                }

                if (!switch.isEnabled) {
                    return@setOnClickListener
                }

                toggleChannelView.channel?.let {
                    onChannelToggled(it, !switch.isChecked)
                }
            }

            itemView.setOnLongClickListener {
                toggleChannelView.setMoving()

                return@setOnLongClickListener true
            }

            itemView.setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    return@setOnKeyListener false
                }

                // Only handle keyDown events here
                if (event.action != KeyEvent.ACTION_DOWN) {
                    return@setOnKeyListener toggleChannelView.moving
                }

                val list = currentList.toMutableList()
                val pos = bindingAdapterPosition

                when (keyCode) {
                    KeyEvent.KEYCODE_BACK -> {
                        if (toggleChannelView.moving) {
                            toggleChannelView.setMoveDone()
                            return@setOnKeyListener true
                        }
                        return@setOnKeyListener false
                    }

                    KeyEvent.KEYCODE_DPAD_UP -> {
                        if (toggleChannelView.moving) {
                            if (pos == 0) {
                                return@setOnKeyListener true
                            }
                            Collections.swap(list, pos, pos - 1)
                            onOrderChanged(list.map { it.first })
                            return@setOnKeyListener true
                        }
                    }

                    KeyEvent.KEYCODE_DPAD_DOWN -> {
                        if (toggleChannelView.moving) {
                            if (pos == list.size - 1) {
                                return@setOnKeyListener true
                            }
                            Collections.swap(list, pos, pos + 1)
                            onOrderChanged(list.map { it.first })
                            return@setOnKeyListener true
                        }
                    }
                }

                return@setOnKeyListener false
            }
        }

        fun bind(channel: Pair<Channel, Boolean>) {
            toggleChannelView.setData(channel.first, channel.second)

            if (channel.first.id == InternalChannel.FAVORITE_APPS.id) {
                toggleChannelView.disableToggle()
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        ToggleChannelView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    )

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<Pair<Channel, Boolean>>() {
            override fun areItemsTheSame(
                oldItem: Pair<Channel, Boolean>,
                newItem: Pair<Channel, Boolean>,
            ) = oldItem.first.id == newItem.first.id

            override fun areContentsTheSame(
                oldItem: Pair<Channel, Boolean>,
                newItem: Pair<Channel, Boolean>,
            ) = compareValuesBy(
                oldItem, newItem,
                { it.first.title },
                { it.second },
            ) == 0
        }
    }
}
