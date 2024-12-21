/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.animation.AnimatorInflater
import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.tvprovider.media.tv.BasePreviewProgram
import coil.load
import com.google.android.material.progressindicator.LinearProgressIndicator
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.ext.getAttributeResourceId

class WatchNextCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Card(context, attrs, defStyleAttr) {
    // Views
    private val bannerView: ImageView by lazy { findViewById(R.id.app_banner)!! }
    private val title: TextView by lazy { findViewById(R.id.title)!! }
    private val progressView: LinearProgressIndicator by lazy { findViewById(R.id.watch_progress)!! }

    init {
        inflate(context, R.layout.watch_next_card, this)

        stateListAnimator =
            AnimatorInflater.loadStateListAnimator(context, R.animator.app_card_state_animator)

        setOnFocusChangeListener { _, hasFocus ->
            title.isInvisible = !hasFocus
            if (hasFocus) {
                title.postDelayed({ title.isSelected = true }, 2000)
            } else {
                title.isSelected = false
            }
        }
    }

    @Suppress("RestrictedApi")
    fun setInfo(info: BasePreviewProgram) {
        title.isInvisible = true
        label = info.title
        bannerView.isVisible = true
        launchIntent = info.intent
        title.text = info.title

        if (info.lastPlaybackPositionMillis != -1 && info.durationMillis != -1) {
            val percentWatched =
                ((info.lastPlaybackPositionMillis.toDouble() / info.durationMillis) * 100).toInt()
            if (percentWatched > 3) {
                progressView.progress = percentWatched
                progressView.isVisible = true
            }
        }

        bannerView.load(info.posterArtUri) {
            placeholder(
                context.getAttributeResourceId(
                    com.google.android.material.R.attr.colorSecondaryContainer
                )
            )
            crossfade(500)
        }
    }
}
