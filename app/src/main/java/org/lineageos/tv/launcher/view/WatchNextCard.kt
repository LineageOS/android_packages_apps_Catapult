/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.animation.AnimatorInflater
import android.content.Context
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.tvprovider.media.tv.BasePreviewProgram
import androidx.tvprovider.media.tv.TvContractCompat
import coil.load
import coil.size.Scale
import coil.transform.RoundedCornersTransformation
import com.google.android.material.progressindicator.LinearProgressIndicator
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.ext.getAttributeResourceId

class WatchNextCard @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Card(context, attrs, defStyleAttr) {
    // Views
    private val bannerBackgroundView: ImageView by lazy { findViewById(R.id.app_banner_background)!! }
    private val bannerView: ImageView by lazy { findViewById(R.id.app_banner)!! }
    private val smallBannerView: ImageView by lazy { findViewById(R.id.app_banner_small)!! }
    private val title: TextView by lazy { findViewById(R.id.title)!! }
    private val progressView: LinearProgressIndicator by lazy { findViewById(R.id.watch_progress)!! }

    init {
        inflate(context, R.layout.watch_next_card, this)

        stateListAnimator =
            AnimatorInflater.loadStateListAnimator(context, R.animator.app_card_state_animator)

        setupNameMarquee()
    }

    private fun setupNameMarquee() {
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
        bannerView.isVisible = false
        smallBannerView.isVisible = false
        progressView.isVisible = false

        title.isInvisible = true
        label = info.title
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

        // Other than 16:9, use blurred background and smaller art
        if (info.posterArtAspectRatio != TvContractCompat.PreviewPrograms.ASPECT_RATIO_16_9) {
            bannerView.isVisible = false
            smallBannerView.isVisible = true
            bannerBackgroundView.load(info.posterArtUri) {
                placeholder(
                    context.getAttributeResourceId(
                        com.google.android.material.R.attr.colorSecondaryContainer
                    )
                )
                crossfade(500)
                scale(Scale.FILL)
                listener(onSuccess = { _, _ ->
                    bannerBackgroundView.setRenderEffect(
                        RenderEffect.createBlurEffect(
                            25f, 25f, Shader.TileMode.CLAMP
                        )
                    )

                    bannerBackgroundView.colorFilter = PorterDuffColorFilter(
                        ContextCompat.getColor(context, R.color.watchNextCardFilter),
                        PorterDuff.Mode.SRC_ATOP
                    )
                }
                )
            }

            smallBannerView.load(info.posterArtUri) {
                crossfade(500)
                transformations(RoundedCornersTransformation(5F))
            }

            return
        }

        bannerView.isVisible = true
        smallBannerView.isVisible = false
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
