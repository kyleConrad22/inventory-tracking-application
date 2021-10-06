package com.example.rusalqrandbarcodescanner

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import org.junit.Assert.assertFalse;
import org.junit.Assert.assertTrue;
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowNetworkCapabilities

class HttpRequestHandlerTest {

    @RunWith(RobolectricTestRunner::class)
    class ConnectivityHandlerTest {

        private val connectivityHandler : ConnectivityHandler = ConnectivityHandler(getApplicationContext())

        private lateinit var connectivityManager: ConnectivityManager
        private lateinit var networkCapabilities : NetworkCapabilities

        @Before
        fun init() {
            connectivityManager = getApplicationContext<Context>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            networkCapabilities = ShadowNetworkCapabilities.newInstance()
        }

        @Test
        fun `Given no cellular or wifi connection, When connection requested, Then return false`() {
            shadowOf(networkCapabilities)
            shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)

            assertFalse(connectivityHandler.isConnectedOrConnecting())
        }

        @Test
        fun `Given cellular connection, When connection requested, Then return true`() {
            shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)

            assertTrue(connectivityHandler.isConnectedOrConnecting())
        }

        @Test
        fun `Given wifi connection, When connection requested, Then return true`() {
            shadowOf(networkCapabilities).addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            shadowOf(connectivityManager).setNetworkCapabilities(connectivityManager.activeNetwork, networkCapabilities)

            assertTrue(connectivityHandler.isConnectedOrConnecting())
        }
    }

    @Test
    fun `Given no connection made, When connecting to API, Then return false`() {

    }

    @Test
    fun `Given connection made, When connecting to API, Then return true`() {

    }

    @Test
    fun `Given no valid entries found, When parsing API result, Then return false`() {

    }

    @Test
    fun `Given valid entries found, When parsing API result, Then return true`() {

    }

    @Test
    fun `Given empty request body, When posting update to API, Then return false`() {

    }

    @Test
    fun `Given non-empty request body, When posting update to API, Then return true`() {

    }
}