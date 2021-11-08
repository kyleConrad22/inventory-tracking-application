package com.example.rusalqrandbarcodescanner.services

import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object ConnectivityHandler {

    lateinit var connectivityManager : ConnectivityManager

    fun isConnectedOrConnecting() : Boolean {
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
            return hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        }
        return false
    }

}
