/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

sealed class NetworkState(val network: Network) {
    class Available(network: Network) : NetworkState(network)
    class Lost(network: Network) : NetworkState(network)
    class CapabilitiesChanged(network: Network, val networkCapabilities: NetworkCapabilities) :
        NetworkState(network)
}

fun ConnectivityManager.networkCallbackFlow(request: NetworkRequest) =
    callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(NetworkState.Available(network))
            }

            override fun onLost(network: Network) {
                trySend(NetworkState.Lost(network))
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                trySend(NetworkState.CapabilitiesChanged(network, networkCapabilities))
            }
        }

        registerNetworkCallback(request, callback)

        awaitClose {
            unregisterNetworkCallback(callback)
        }
    }
