/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import android.database.Cursor

fun <T> Cursor?.mapEachRow(
    projection: Array<String>? = null,
    mapping: (Cursor, Array<Int>) -> T,
) = this?.use { cursor ->
    if (!cursor.moveToFirst()) {
        return@use emptyList()
    }

    val indexCache = projection?.map { column ->
        cursor.getColumnIndexOrThrow(column)
    }?.toTypedArray() ?: arrayOf()

    val data = mutableListOf<T>()
    do {
        data.add(mapping(cursor, indexCache))
    } while (cursor.moveToNext())

    data.toList()
} ?: emptyList()
