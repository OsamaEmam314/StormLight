package com.example.stormlight.utilities

import android.content.Context
import android.net.NetworkCapabilities

object NetworkUtils {
    fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(
                connectivityManager.activeNetwork ?: return false
            ) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)


    }
}
