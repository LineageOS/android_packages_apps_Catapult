/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.tvprovider.media.tv.TvContractCompat

/**
 * App's permissions utils.
 */
object PermissionsUtils {
    fun mainPermissionsGranted(context: Context) = permissionsGranted(context, mainPermissions)

    private fun permissionGranted(context: Context, permission: String) =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    private fun permissionsGranted(context: Context, permissions: Array<String>) = permissions.all {
        permissionGranted(context, it)
    }

    /**
     * Permissions required to run the app
     */
    @Suppress("RestrictedApi")
    val mainPermissions = arrayOf(
        TvContractCompat.PERMISSION_READ_TV_LISTINGS,
    )
}
