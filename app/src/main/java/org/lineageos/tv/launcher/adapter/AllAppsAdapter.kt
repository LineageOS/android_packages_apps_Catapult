/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.adapter

import android.view.ViewGroup
import org.lineageos.tv.launcher.model.LeanbackAppInfo
import org.lineageos.tv.launcher.view.AppCard

class AllAppsAdapter : TvAdapter<LeanbackAppInfo, AppCard>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        AppCard(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
        }
    )

    override fun handleLongClick(card: AppCard): Boolean {
        card.showPopupMenu()
        return true
    }
}
