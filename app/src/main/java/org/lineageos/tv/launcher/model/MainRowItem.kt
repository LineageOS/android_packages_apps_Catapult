/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

import androidx.recyclerview.widget.RecyclerView

class MainRowItem(
    val label: String,
    val adapter: RecyclerView.Adapter<out RecyclerView.ViewHolder>,
)
