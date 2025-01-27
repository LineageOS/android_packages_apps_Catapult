/*
 * SPDX-FileCopyrightText: 2023-2024 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.flow

import kotlinx.coroutines.flow.Flow

interface QueryFlow<T> {
    /**
     * A flow of the data specified by the query
     */
    fun flowData(): Flow<List<T>>
}
