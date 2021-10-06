package com.example.rusalqrandbarcodescanner

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.ui.platform.LocalContext
import com.google.android.datatransport.cct.internal.NetworkConnectionInfo

class ConnectivityHandler(context : Context) {

    private val connectivityManager : ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun isConnectedOrConnecting() : Boolean {
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
            return hasTransport(NetworkCapabilities.TRANSPORT_WIFI) || hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
        }
        return false
    }

}
