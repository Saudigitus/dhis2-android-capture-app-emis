package org.saudigitus.emis.utils

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import timber.log.Timber

object NetworkUtils {
    fun isOnline(context: Context): Boolean {
        var isOnline = false
        try {
            val manager = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager
            if (manager != null) {
                val netInfo = manager.activeNetworkInfo
                isOnline = netInfo != null && netInfo.isConnectedOrConnecting
            }
        } catch (ex: Exception) {
            Timber.e(ex)
        }
        return isOnline
    }
}