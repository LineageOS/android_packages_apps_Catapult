/*
 * SPDX-FileCopyrightText: The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher.ext

import android.app.role.RoleManager

fun RoleManager.roleCanBeRequested(roleName: String) =
    isRoleAvailable(roleName) && !isRoleHeld(roleName)
