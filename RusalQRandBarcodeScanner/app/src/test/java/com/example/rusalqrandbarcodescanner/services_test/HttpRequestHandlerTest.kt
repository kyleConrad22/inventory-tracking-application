package com.example.rusalqrandbarcodescanner.services_test

import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.test.KoinTest
import org.mockserver.configuration.ConfigurationProperties
import org.mockserver.netty.MockServer
import java.net.URL
import java.util.*
import kotlin.collections.HashMap
import kotlin.test.assertEquals

class HttpRequestHandlerTest : KoinTest {

    private lateinit var server : MockServer

    @Before
    fun initTest() {
        ConfigurationProperties.initializationJsonPath("com/example/rusalqrandbarcodescanner/resources_test/initialization.json")
        server = MockServer(2018)
    }

    @After
    fun shutdown() {
        server.stop()
    }

    @Test
    fun `Given no connection made, When connecting to API, Then return false`() = runBlocking {
        assertFalse(HttpRequestHandler().connectToUrl(URL("http", "localhost", 2018, "/null")))
    }

    @Test
    fun `Given connection made, When connecting to API, Then return true`() = runBlocking {
        assertTrue(HttpRequestHandler().connectToUrl(URL("http", "localhost", 2018, "/empty")))
    }

    class ApiParsingTest : KoinTest {

        private lateinit var httpRequestHandler : HttpRequestHandler

        @Before
        fun initApiTest() {
            httpRequestHandler = HttpRequestHandler()
        }

        @After
         fun disconnect() {
             httpRequestHandler.urlConnection.disconnect()
         }

        @Test
        fun `Given no valid entries found, When parsing API result, Then return empty HashMap`() = runBlocking {
            httpRequestHandler.connectToUrl(URL("http", "localhost", 2018, "/empty"))

            val expVal = listOf<HashMap<String, String>>()
            val retVal = httpRequestHandler.parseApiResponse(httpRequestHandler.getApiResponse());

            assertEquals(expVal, retVal, "Expected $expVal but returned $retVal")
        }

        @Test
        fun `Given entry found with single field, When parsing API result, Then return proper key-val pair`()  = runBlocking {
            httpRequestHandler.connectToUrl(URL("http", "localhost", 2018, "/singleField"))

            val expVal = listOf<HashMap<String, String>>(HashMap())
            expVal.last()["heatNum"]="17201601"
            val retVal = httpRequestHandler.parseApiResponse(httpRequestHandler.getApiResponse());

            assertEquals(expVal, retVal, "Expected $expVal but returned $retVal")
        }

        @Test
        fun `Given entry found with multiple fields, When parsing API result, Then return proper key-val pair`() = runBlocking {
            httpRequestHandler.connectToUrl(URL("http", "localhost", 2018, "/singleLineMultipleFields"))

            val expVal = listOf<HashMap<String, String>>(HashMap())
            expVal.last()["heatNum"]="17201601"; expVal.last()["packageNum"]="17"
            val retVal = httpRequestHandler.parseApiResponse(httpRequestHandler.getApiResponse());

            assertEquals(expVal, retVal, "Expected $expVal but returned $retVal")
        }

        @Test
        fun `Given multiple entries found, When posting API result, Then return proper all proper key-val pairs`() = runBlocking {
            httpRequestHandler.connectToUrl(URL("http", "localhost", 2018, "/multipleLines"))

            val expVal = listOf<HashMap<String, String>>(HashMap())
            expVal[0]["heatNum"]="17202102"
            expVal.last()["heatNum"]="1453264"
            val retVal = httpRequestHandler.parseApiResponse(httpRequestHandler.getApiResponse());

            assertEquals(expVal, retVal, "Expected $expVal but returned $retVal")
        }
    }
}