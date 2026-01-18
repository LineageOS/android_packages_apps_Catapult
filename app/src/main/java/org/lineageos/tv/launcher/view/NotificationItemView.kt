/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.view

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.Notification
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.service.notification.StatusBarNotification
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import org.lineageos.tv.launcher.R

class NotificationItemView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    enum class SwipeStatus {
        NONE, LEFT, RIGHT
    }

    var statusBarNotification: StatusBarNotification? = null

    var swipeStatus = SwipeStatus.NONE

    // Views
    private val dismissStartButton: MaterialButton by lazy { findViewById(R.id.dismiss_start)!! }
    private val dismissEndButton: MaterialButton by lazy { findViewById(R.id.dismiss_end)!! }
    private val contentContainer: LinearLayout by lazy { findViewById(R.id.notification_content_container)!! }
    private val icon: ImageView by lazy { findViewById(R.id.icon)!! }
    private val title: TextView by lazy { findViewById(R.id.notification_title)!! }
    private val details: TextView by lazy { findViewById(R.id.notification_details)!! }

    init {
        LayoutInflater.from(context).inflate(R.layout.notification_item_view, this)
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        clipChildren = false
        clipToPadding = false
    }

    fun animateDismissLeft() {
        if (swipeStatus == SwipeStatus.RIGHT) {
            // Undo swipe
            animateCloseDismiss()
            swipeStatus = SwipeStatus.NONE
        } else {
            // Do swipe
            dismissEndButton.visibility = View.VISIBLE
            animateOpenDismiss(-dismissEndButton.width.toFloat())
            swipeStatus = SwipeStatus.LEFT
        }
    }

    fun animateDismissRight() {
        if (swipeStatus == SwipeStatus.LEFT) {
            // Undo swipe
            animateCloseDismiss()
            swipeStatus = SwipeStatus.NONE
        } else {
            // Do swipe
            dismissStartButton.visibility = View.VISIBLE
            animateOpenDismiss(dismissStartButton.width.toFloat())
            swipeStatus = SwipeStatus.RIGHT
        }
    }

    fun resetState() {
        swipeStatus = SwipeStatus.NONE

        val rippleDrawable = contentContainer.background as? RippleDrawable
        val backgroundShape = (rippleDrawable?.getDrawable(0) as? GradientDrawable)
        backgroundShape?.cornerRadius = resources.getDimension(R.dimen.notification_corner_radius)
    }

    fun animateCloseDismiss() {
        swipeStatus = SwipeStatus.NONE
        val rippleDrawable = contentContainer.background as? RippleDrawable
        val backgroundShape = (rippleDrawable?.getDrawable(0) as? GradientDrawable)

        val translationAnimator = ObjectAnimator.ofFloat(
            contentContainer,
            "translationX",
            contentContainer.translationX,
            0f
        ).apply {
            this.duration = ANIM_DURATION
        }

        val cornerRadiusAnimator =
            ValueAnimator.ofFloat(0f, resources.getDimension(R.dimen.notification_corner_radius))
                .apply {
                    this.duration = duration
                    addUpdateListener { animator ->
                        backgroundShape?.cornerRadius = (animator.animatedValue as Float)
                    }
                }

        AnimatorSet().apply {
            playTogether(
                cornerRadiusAnimator,
                translationAnimator
            )
        }.start()
    }

    private fun animateOpenDismiss(translationAmount: Float) {
        val rippleDrawable = contentContainer.background as? RippleDrawable
        val backgroundShape = (rippleDrawable?.getDrawable(0) as? GradientDrawable)

        val translationAnimator = ObjectAnimator.ofFloat(
            contentContainer,
            "translationX",
            contentContainer.translationX,
            contentContainer.translationX + translationAmount
        ).apply {
            this.duration = ANIM_DURATION
        }

        val cornerRadiusAnimator =
            ValueAnimator.ofFloat(resources.getDimension(R.dimen.notification_corner_radius), 0f)
                .apply {
                    this.duration = duration
                    addUpdateListener { animator ->
                        backgroundShape?.cornerRadius = (animator.animatedValue as Float)
                    }
                }

        AnimatorSet().apply {
            playTogether(
                cornerRadiusAnimator,
                translationAnimator
            )
        }.start()
    }

    fun setNotification(sbn: StatusBarNotification) {
        statusBarNotification = sbn
        val notification = sbn.notification
        title.text = notification.extras.getString(Notification.EXTRA_TITLE)
        details.text =
            notification.extras.getString(Notification.EXTRA_TEXT)
        icon.setImageDrawable(notification.smallIcon.loadDrawable(context))
    }

    companion object {
        private const val ANIM_DURATION: Long = 200
    }
}
