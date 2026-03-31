/*
 * SPDX-FileCopyrightText: 2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package android.app

class Notification {
    class TvExtender(private val notification: Notification) {
        var isAvailableOnTv: Boolean = false
    }
}
