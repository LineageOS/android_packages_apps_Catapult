package org.lineageos.tv.launcher.utils

import android.content.Context
import android.content.Intent
import org.lineageos.tv.launcher.model.AppInfo
import org.lineageos.tv.launcher.model.Launchable

object AppManager {
    fun getInstalledApps(context: Context): ArrayList<Launchable> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(intent, 0)
        val appsList: ArrayList<Launchable> = ArrayList()
        for (app in apps) {
            val appInfo = AppInfo(
                app.loadLabel(pm).toString(),
                app.activityInfo.packageName,
                app.activityInfo.loadIcon(pm),
                context
            )
            appsList.add(appInfo)
        }

        return appsList
    }

    fun removeFavoriteApp(context: Context, app: Launchable) {
        val sharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        val favoritesSet =
            sharedPreferences.getStringSet("favoriteApps", HashSet()) ?: HashSet()
        val newFavoritesSet = HashSet(favoritesSet) // Make a copy
        newFavoritesSet.remove(app.mPackageName)
        val editor = sharedPreferences.edit()
        editor.putStringSet("favoriteApps", newFavoritesSet)
        editor.apply()
    }

    fun addFavoriteApp(context: Context, app: Launchable) {
        val sharedPreferences = context.getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        val favoritesSet =
            sharedPreferences.getStringSet("favoriteApps", HashSet()) ?: HashSet()
        val newFavoritesSet = HashSet(favoritesSet) // Make a copy
        newFavoritesSet.add(app.mPackageName)
        val editor = sharedPreferences.edit()
        editor.putStringSet("favoriteApps", newFavoritesSet)
        editor.apply()
    }

    fun getFavoriteApps(context: Context): Set<String> {
        val sharedPreferences =
            context.getSharedPreferences("Favorites", Context.MODE_PRIVATE)
        return sharedPreferences.getStringSet("favoriteApps", HashSet()) ?: HashSet()
    }
}