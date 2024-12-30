/*
 * SPDX-FileCopyrightText: 2024-2025 The LineageOS Project
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.tv.launcher

import android.os.Bundle
import androidx.activity.viewModels
import androidx.leanback.widget.VerticalGridView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.lineageos.tv.launcher.adapter.ModifyFavoritesAdapter
import org.lineageos.tv.launcher.utils.AppManager
import org.lineageos.tv.launcher.utils.PermissionsGatedCallback
import org.lineageos.tv.launcher.viewmodels.AddFavoriteViewModel

class AddFavoriteActivity : ModalActivity(R.layout.activity_add_favorite) {
    // View models
    private val model: AddFavoriteViewModel by viewModels()

    // Views
    private val allAppsGridView by lazy { findViewById<VerticalGridView>(R.id.all_apps_add_grid)!! }

    // Adapters
    private val allAppsAdapter by lazy { ModifyFavoritesAdapter() }

    private val permissionsGatedCallback = PermissionsGatedCallback(this) {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.appsToFavorites.collectLatest {
                    allAppsAdapter.submitList(it)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        allAppsGridView.adapter = allAppsAdapter

        allAppsAdapter.onFavoriteChanged = { packageName, favorite ->
            AppManager.toggleFavoriteApp(this@AddFavoriteActivity, packageName, favorite)
        }

        permissionsGatedCallback.runAfterPermissionsCheck()
    }
}
