/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import org.lineageos.tv.launcher.R

class SectionHeaderAdapter(
    @StringRes private val titleResId: Int,
) : RecyclerView.Adapter<SectionHeaderAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.favorites_section_header, parent, false) as TextView
    )

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.titleTextView.setText(titleResId)
    }

    override fun getItemCount() = 1

    class ViewHolder(val titleTextView: TextView) : RecyclerView.ViewHolder(titleTextView)
}
