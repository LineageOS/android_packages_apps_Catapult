/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher

import android.app.ActivityOptions
import android.app.PendingIntent
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.content.pm.ApplicationInfo.FLAG_SYSTEM
import android.icu.text.DateFormat
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.TransportInfo
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.os.SystemClock
import android.provider.Settings
import android.service.notification.StatusBarNotification
import android.text.SpannableString
import android.util.Log
import android.view.ContextMenu
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import android.view.WindowManagerGlobal
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.content.res.AppCompatResources
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import org.lineageos.tv.launcher.ext.NetworkState
import org.lineageos.tv.launcher.ext.networkCallbackFlow
import org.lineageos.tv.launcher.notification.NotificationAdapter
import org.lineageos.tv.launcher.notification.NotificationUtils
import org.lineageos.tv.launcher.notification.ServiceConnectionState
import org.lineageos.tv.launcher.utils.AppManager
import org.lineageos.tv.launcher.utils.DeveloperOptions
import org.lineageos.tv.launcher.view.NotificationItemView
import org.lineageos.tv.launcher.view.TwoLineButton
import org.lineageos.tv.launcher.viewmodels.NotificationViewModel
import java.util.Calendar

class SystemOptionsActivity : ModalActivity(R.layout.activity_system_options),
    NotificationAdapter.OnItemActionListener {
    // View model
    private val notificationViewModel: NotificationViewModel by viewModels()

    // Views
    private val allowNotificationAccessMaterialButton by lazy { findViewById<MaterialButton>(R.id.allowNotificationAccessMaterialButton)!! }
    private val bluetoothTwoLineButton by lazy { findViewById<TwoLineButton>(R.id.bluetoothTwoLineButton)!! }
    private val dateTextView by lazy { findViewById<TextView>(R.id.dateTextView)!! }
    private val networkTwoLineButton by lazy { findViewById<TwoLineButton>(R.id.networkTwoLineButton)!! }
    private val noNotificationAccessLinearLayout by lazy { findViewById<LinearLayout>(R.id.noNotificationAccessLinearLayout)!! }
    private val noNotificationsTextView by lazy { findViewById<TextView>(R.id.noNotificationsTextView)!! }
    private val notificationsVerticalGridView by lazy { findViewById<VerticalGridView>(R.id.notificationsVerticalGridView)!! }
    private val powerMaterialButton by lazy { findViewById<MaterialButton>(R.id.powerMaterialButton)!! }
    private val settingsButton by lazy { findViewById<MaterialButton>(R.id.settingsMaterialButton)!! }
    private val sleepMaterialButton by lazy { findViewById<MaterialButton>(R.id.sleepMaterialButton)!! }

    private val notificationAdapter: NotificationAdapter by lazy { NotificationAdapter(this, this) }

    private val connectivityManager by lazy { getSystemService(ConnectivityManager::class.java)!! }

    private lateinit var developerOptions: DeveloperOptions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Animate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                OVERRIDE_TRANSITION_OPEN,
                R.anim.slide_in_right,
                R.anim.slide_out_right
            )
            overrideActivityTransition(
                OVERRIDE_TRANSITION_CLOSE,
                R.anim.slide_in_right,
                R.anim.slide_out_right
            )
        }

        // Date
        val currentDate = Calendar.getInstance().time
        dateTextView.text = DateFormat.getPatternInstance(DateFormat.YEAR_ABBR_MONTH_WEEKDAY_DAY)
            .format(currentDate)

        // Wifi & Bluetooth
        setNetworkButton()
        setBluetoothButton()

        settingsButton.setOnClickListener {
            startActivity(SETTINGS)
        }

        allowNotificationAccessMaterialButton.setOnClickListener {
            startActivity(NOTIFICATION_SETTINGS)
        }

        notificationsVerticalGridView.adapter = notificationAdapter

        developerOptions = DeveloperOptions(this)

        if (AppManager.isSystemApp(this)) {
            sleepMaterialButton.setOnClickListener {
                val pm: PowerManager = getSystemService(PowerManager::class.java) as PowerManager
                pm.goToSleep(
                    SystemClock.uptimeMillis(),
                    PowerManager.GO_TO_SLEEP_REASON_POWER_BUTTON,
                    0
                )
            }

            powerMaterialButton.setOnClickListener {
                val wm = WindowManagerGlobal.getWindowManagerService()
                wm?.showGlobalActions()
            }

            // Developer options context menu
            if (isDeveloperOptionsEnabled()) {
                registerForContextMenu(settingsButton)
            }
        } else {
            sleepMaterialButton.visibility = View.GONE
            powerMaterialButton.visibility = View.GONE
        }

        // WIFI callbacks
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        lifecycleScope.launch {
            connectivityManager.networkCallbackFlow(request).collect {
                when (it) {
                    is NetworkState.Available -> setNetworkButton(
                        capabilities = connectivityManager.getNetworkCapabilities(it.network)
                    )

                    is NetworkState.Lost -> setNetworkButton(
                        capabilities = connectivityManager.getNetworkCapabilities(it.network)
                    )

                    is NetworkState.CapabilitiesChanged ->
                        setNetworkButton(
                            transportInfo = it.networkCapabilities.transportInfo,
                            capabilities = it.networkCapabilities
                        )
                }
            }
        }

        networkTwoLineButton.setOnClickListener {
            startActivity(WIFI_SETTINGS)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                notificationViewModel.state.collect { state ->
                    when (state) {
                        is ServiceConnectionState.Connected -> {
                            // Ignore
                        }

                        is ServiceConnectionState.Disconnected -> {
                            if (NotificationUtils.notificationPermissionGranted(this@SystemOptionsActivity)) {
                                notificationAdapter.submitList(emptyList())
                                noNotificationsTextView.visibility = View.VISIBLE
                            } else {
                                noNotificationAccessLinearLayout.visibility = View.VISIBLE
                                noNotificationsTextView.visibility = View.GONE
                            }
                            notificationsVerticalGridView.visibility = View.GONE
                        }

                        is ServiceConnectionState.Notifications -> {
                            if (state.notifications.isEmpty() || state.currentRanking == null) {
                                noNotificationsTextView.visibility = View.VISIBLE
                                notificationsVerticalGridView.visibility = View.GONE
                                return@collect
                            }

                            val statusBarNotifications = ArrayList<StatusBarNotification>()
                            for (key in state.currentRanking.orderedKeys) {
                                val sbn: StatusBarNotification? = state.notifications[key]
                                if (sbn != null) {
                                    statusBarNotifications.add(sbn)
                                }
                            }

                            notificationAdapter.submitList(statusBarNotifications)
                            noNotificationsTextView.visibility = View.GONE
                            notificationsVerticalGridView.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (!NotificationUtils.notificationPermissionGranted(this)) {
            noNotificationAccessLinearLayout.visibility = View.VISIBLE
            noNotificationsTextView.visibility = View.GONE
            notificationsVerticalGridView.visibility = View.GONE
            return
        }

        notificationViewModel.bindService(this)
    }

    override fun onResume() {
        super.onResume()
        if (NotificationUtils.notificationPermissionGranted(this)) {
            noNotificationAccessLinearLayout.visibility = View.GONE
        } else {
            noNotificationAccessLinearLayout.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (NotificationUtils.notificationPermissionGranted(this)) {
            notificationViewModel.unbindService(this)
        }
    }

    override fun onCreateContextMenu(menu: ContextMenu?, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.settingsMaterialButton) {
            menuInflater.inflate(R.menu.developer_options, menu)
            val menuItem = menu?.findItem(R.id.toggle_adb_network)
            if (developerOptions.adbOverNetworkEnabled()) {
                menuItem?.title = getString(R.string.disable_adb_network)
            } else {
                menuItem?.title = getString(R.string.enable_adb_network)
            }
        }
    }
    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.toggle_adb_network -> {
                developerOptions.toggleAdbOverNetwork()
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    private fun setNetworkButton(
        transportInfo: TransportInfo? = null,
        capabilities: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(
            connectivityManager.activeNetwork
        )
    ) {
        val networkString: String
        val networkIcon: Int
        if (capabilities == null ||
            !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) ||
            !capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        ) {
            // No internet connection
            networkString = resources.getString(R.string.not_connected)
            networkIcon = R.drawable.ic_wifi_not_connected
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
            // Ethernet connection
            networkString = resources.getString(R.string.connected)
            networkIcon = R.drawable.ic_ethernet
        } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
            // WIFI connection
            if (transportInfo is WifiInfo) {
                val wifiManager = getSystemService(WifiManager::class.java)!!
                val wifiStrength = wifiManager.calculateSignalLevel(transportInfo.rssi)
                networkString = resources.getString(R.string.connected)
                networkIcon = wifiIcons[wifiStrength.coerceIn(0, wifiIcons.size - 1)]
            } else {
                networkString = resources.getString(R.string.not_connected)
                networkIcon = R.drawable.ic_wifi_not_connected
            }
        } else {
            // Unknown transport type
            networkString = resources.getString(R.string.unknown)
            networkIcon = R.drawable.ic_wifi_not_connected
        }

        networkTwoLineButton.icon = AppCompatResources.getDrawable(this, networkIcon)

        val networkSpan =
            SpannableString(resources.getString(R.string.network_status, networkString))
        networkTwoLineButton.setSpan(networkSpan)
    }

    private fun setBluetoothButton() {
        var btString = resources.getString(R.string.disabled)
        val bluetoothAdapter = getSystemService(BluetoothManager::class.java)?.adapter
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
            btString = resources.getString(R.string.enabled)
        }

        val btSpan = SpannableString(resources.getString(R.string.bluetooth_status, btString))
        bluetoothTwoLineButton.setSpan(btSpan)

        bluetoothTwoLineButton.setOnClickListener {
            startActivity(BLUETOOTH_SETTINGS)
        }
    }

    override fun onItemClick(view: NotificationItemView) {
        val notification = view.statusBarNotification?.notification ?: return
        try {
            if (notification.contentIntent != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    val activityOptions = ActivityOptions.makeBasic()
                    activityOptions.setPendingIntentBackgroundActivityStartMode(
                        ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
                    )
                    notification.contentIntent?.send(activityOptions.toBundle())
                } else {
                    notification.contentIntent?.send()
                }
            }

            view.statusBarNotification?.let { cancelNotification(it) }
        } catch (e: PendingIntent.CanceledException) {
            Log.d(
                "SystemOptionsActivity",
                "Pending intent canceled for : ${notification.contentIntent}"
            )
        }
    }

    override fun onKey(view: NotificationItemView, keyCode: Int, event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) {
            return false
        }

        if (view.statusBarNotification?.isOngoing == true) {
            return false
        }

        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                if (view.swipeStatus == NotificationItemView.SwipeStatus.LEFT) {
                    view.resetState()
                    view.statusBarNotification?.let { cancelNotification(it) }
                } else {
                    view.animateDismissLeft()
                }
                return true
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                if (view.swipeStatus == NotificationItemView.SwipeStatus.RIGHT) {
                    view.resetState()
                    view.statusBarNotification?.let { cancelNotification(it) }
                } else {
                    view.animateDismissRight()
                }
                return true
            }

            KeyEvent.KEYCODE_DPAD_CENTER -> {
                if (view.swipeStatus != NotificationItemView.SwipeStatus.NONE) {
                    view.resetState()
                    view.statusBarNotification?.let { cancelNotification(it) }
                    return true
                }
                return false
            }

            else -> {
                if (view.swipeStatus != NotificationItemView.SwipeStatus.NONE) {
                    view.animateCloseDismiss()
                }
                return false
            }
        }
    }

    private fun cancelNotification(sbn: StatusBarNotification) {
        if (NotificationUtils.shouldAutoCancel(sbn.notification)) {
            notificationViewModel.cancelNotification(sbn.key)
        }
    }

    private fun isSystemApp(): Boolean {
        return applicationInfo.flags and FLAG_SYSTEM != 0
    }

    private fun isDeveloperOptionsEnabled(): Boolean {
        return Settings.Secure.getInt(
            this.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
        ) == 1
    }

    companion object {
        val SETTINGS: Intent = Intent(Settings.ACTION_SETTINGS)
        val WIFI_SETTINGS: Intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        val BLUETOOTH_SETTINGS: Intent = Intent().apply {
            setClassName("com.android.tv.settings", "com.android.tv.settings.slice.SliceActivity")
            putExtra(
                "slice_uri",
                "content://com.android.tv.settings.accessories.sliceprovider/general"
            )
        }
        val NOTIFICATION_SETTINGS: Intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)

        val wifiIcons = intArrayOf(
            R.drawable.ic_wifi_signal_0,
            R.drawable.ic_wifi_signal_1,
            R.drawable.ic_wifi_signal_2,
            R.drawable.ic_wifi_signal_3,
            R.drawable.ic_wifi_signal_4
        )
    }
}
