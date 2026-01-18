/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.notification

import android.app.Notification
import android.content.Context
import android.service.notification.StatusBarNotification
import android.view.KeyEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.lineageos.tv.launcher.view.NotificationItemView

class NotificationAdapter(
    private val context: Context,
    private val onItemActionListener: OnItemActionListener
) : ListAdapter<StatusBarNotification, NotificationAdapter.TvNotificationViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TvNotificationViewHolder(
        (::NotificationItemView)(parent.context, null, 0),
    )

    override fun onBindViewHolder(holder: TvNotificationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TvNotificationViewHolder(val view: NotificationItemView) :
        RecyclerView.ViewHolder(view) {
        var item: StatusBarNotification? = null

        init {
            view.setOnClickListener {
                onItemActionListener.onItemClick(view)
            }
            view.setOnKeyListener { _, keyCode, event ->
                onItemActionListener.onKey(view, keyCode, event)
            }
        }

        fun bind(item: StatusBarNotification) {
            this.item = item
            view.setNotification(item)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<StatusBarNotification>() {
        override fun areItemsTheSame(
            oldItem: StatusBarNotification,
            newItem: StatusBarNotification
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: StatusBarNotification,
            newItem: StatusBarNotification
        ): Boolean {
            return oldItem.notification.extras.getString(Notification.EXTRA_TITLE) == newItem.notification.extras.getString(
                Notification.EXTRA_TITLE
            ) &&
                    oldItem.notification.extras.getString(Notification.EXTRA_TEXT) == newItem.notification.extras.getString(
                Notification.EXTRA_TEXT
            ) &&
                    oldItem.notification.smallIcon.resId == newItem.notification.smallIcon.resId
        }
    }

    interface OnItemActionListener {
        fun onItemClick(view: NotificationItemView)
        fun onKey(view: NotificationItemView, keyCode: Int, event: KeyEvent): Boolean
    }
}
