package org.lineageos.tv.launcher.utils

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.tvprovider.media.tv.TvContractCompat
import kotlinx.coroutines.delay

class Test {
    companion object {
        fun postDelayedAddProgram(context: Context, delayMillis: Long) {
            Handler(Looper.getMainLooper()).postDelayed({
                // Your action to execute after the delay
                addTestWatchNextProgram(context)  // Example: Add the program
            }, delayMillis)
        }

        fun postDelayedRemoveProgram(context: Context, delayMillis: Long) {
            Handler(Looper.getMainLooper()).postDelayed({
                for (i in 1..50) removeTestWatchNextProgram(context, i.toLong())
            }, delayMillis)
        }

        @SuppressLint("RestrictedApi")
        fun addTestWatchNextProgram(context: Context) {
            val contentResolver = context.contentResolver

            // Create a new Watch Next program
            val watchNextProgram = ContentValues().apply {
                put(TvContractCompat.WatchNextPrograms.COLUMN_TITLE, "Test Watch Next Program")
                put(
                    TvContractCompat.WatchNextPrograms.COLUMN_SHORT_DESCRIPTION,
                    "This is a test program."
                )
                put(TvContractCompat.WatchNextPrograms.COLUMN_CONTENT_ID, "content_id")
                put(
                    TvContractCompat.WatchNextPrograms.COLUMN_INTERNAL_PROVIDER_ID,
                    "test_provider_id"
                )
                put(
                    TvContractCompat.WatchNextPrograms.COLUMN_POSTER_ART_URI,
                    "https://prod-ripcut-delivery.disney-plus.net/v1/variant/disney/D2488E934CB4B1D5ED97F3CE951E9A1483ECC4BBC1C9F650F397771EA40345E1/scale?width=544&height=306&format=jpeg"
                )
                put(TvContractCompat.WatchNextPrograms.COLUMN_PACKAGE_NAME, context.packageName)
                put(TvContractCompat.WatchNextPrograms.COLUMN_LAST_PLAYBACK_POSITION_MILLIS, 20000)
                put(TvContractCompat.WatchNextPrograms.COLUMN_DURATION_MILLIS, 60000) // 60 seconds
                put(
                    TvContractCompat.WatchNextPrograms.COLUMN_TYPE,
                    TvContractCompat.WatchNextPrograms.TYPE_CLIP
                )
            }

            // Insert the program into the Watch Next content provider
            val uri = contentResolver.insert(
                TvContractCompat.WatchNextPrograms.CONTENT_URI,
                watchNextProgram
            )
            if (uri != null) {
                android.util.Log.d("WatchNext", "Test Watch Next Program added successfully: $uri")
            } else {
                android.util.Log.e("WatchNext", "Failed to add Test Watch Next Program")
            }
        }

        @SuppressLint("RestrictedApi")
        fun removeTestWatchNextProgram(context: Context, watchNextProgramId: Long) {
            context.contentResolver
                .delete(
                    TvContractCompat.buildWatchNextProgramUri(watchNextProgramId),
                    null,
                    null
                )
        }
    }
}