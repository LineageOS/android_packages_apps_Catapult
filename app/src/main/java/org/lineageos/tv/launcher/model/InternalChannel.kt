/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.model

enum class InternalChannel {
    FAVORITE_APPS,
    ALL_APPS,
    WATCH_NEXT;

    val id = -0x000000000000DEADL - ((ordinal + 1) shl 16)
}
