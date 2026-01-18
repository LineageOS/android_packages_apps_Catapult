/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

fun IntentFilter.broadcastFlow(
    context: Context,
    sendOnStarted: Boolean = false,
) = callbackFlow {
    val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            launch(Dispatchers.IO) {
                runCatching {
                    trySend(intent)
                }
            }
        }
    }

    ContextCompat.registerReceiver(
        context, broadcastReceiver, this@broadcastFlow, ContextCompat.RECEIVER_NOT_EXPORTED
    )

    // Send an empty intent if requested
    if (sendOnStarted) {
        launch(Dispatchers.IO) {
            runCatching {
                trySend(null)
            }
        }
    }

    awaitClose {
        context.unregisterReceiver(broadcastReceiver)
    }
}
