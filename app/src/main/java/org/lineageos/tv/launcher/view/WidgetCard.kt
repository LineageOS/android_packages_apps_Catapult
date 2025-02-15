package org.lineageos.tv.launcher.view

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.widget.FrameLayout
import org.lineageos.tv.launcher.R
import org.lineageos.tv.launcher.model.Launchable
import org.lineageos.tv.launcher.model.WidgetInfo


class WidgetCard @JvmOverloads constructor(
    private val appWidgetHost: AppWidgetHost, private val appWidgetId: Int,
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FavoriteRowCard(context, attrs, defStyleAttr) {

    init {
        inflate(context, R.layout.widget_card, this)
    }

    override fun setCardInfo(appInfo: Launchable) {
        if (appInfo !is WidgetInfo) return
        if (AppWidgetManager.getInstance(context)
                .bindAppWidgetIdIfAllowed(appWidgetId, appInfo.appWidgetProviderInfo.provider)
        ) {
            val hostView =
                appWidgetHost.createView(context, appWidgetId, appInfo.appWidgetProviderInfo)
            val widgetParentView = findViewById<FrameLayout>(R.id.widget_container)
            widgetParentView?.addView(hostView)
        } else {
            // Action android.appwidget.action.APPWIDGET_BIND is not available on TV.
        }

        // Setup configuration activity
        val configActivity = appInfo.appWidgetProviderInfo.configure
        launchIntent = Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE).apply {
            component = ComponentName(
                appInfo.appWidgetProviderInfo.provider.packageName,
                configActivity.className
            )
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
    }
}