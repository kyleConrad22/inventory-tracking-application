package com.example.rusalqrandbarcodescanner.services

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.*
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object HttpRequestHandler {

    private const val WEB_API: String = "http://45.22.122.47:8081/api/rusal"
    lateinit var requestQueue: RequestQueue
    private var jsonResponse : String = ""

    private fun retrieveInventory(callBack : VolleyCallBack) {
        val stringRequest = StringRequest(Request.Method.GET, WEB_API, { response ->
                print(response)
                jsonResponse = response
                callBack.onSuccess()
        }, { error ->
                error.printStackTrace()
        })

        requestQueue.add(stringRequest)
        }

    private suspend fun confirmShipment(items: List<RusalItem>) =
        withContext(Dispatchers.IO) {
            for (item in items) {

                if (!item.barcode.contains('u')) {
                    updateDatabaseForShipment(item)
                } else {
                    createInventoryItem(item)
                }
            }
        }


    private fun updateDatabaseForShipment(item : RusalItem) {
        val url = "$WEB_API/update"

        val postData : JSONObject = JSONObject()
        try {
            postData.put("heatNum", item.heatNum)
            postData.put("workOrder", item.workOrder)
            postData.put("loadNum", item.loadNum)
            postData.put("loader", item.loader)
            postData.put("loadTime", item.loadTime)
        } catch (e : JSONException) {
            e.printStackTrace()
        }

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, postData, { response ->
            print(response.toString())
        }, { error ->
            error.printStackTrace()
        })

        requestQueue.add(jsonObjectRequest)
    }

    private fun createInventoryItem(item : RusalItem) {

        val moshi : Moshi = Moshi.Builder().build()
        val adapter : JsonAdapter<RusalItem> = moshi.adapter(RusalItem::class.java)

        try {
            val postData = JSONObject(adapter.toJson(item))

            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, WEB_API, postData, { response ->
                print(response)
            }, { error ->
                error.printStackTrace()
            })

            requestQueue.add(jsonObjectRequest)

        } catch (e : JSONException) {
            e.printStackTrace()
        }

    }

    private suspend fun addToRepo(response : String, invRepo : InventoryRepository) {
        val moshi : Moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, RusalItem::class.java)
        val adapter : JsonAdapter<List<RusalItem>> = moshi.adapter(listType)

        val rusalItems = adapter.fromJson(response);

        for (item in rusalItems!!) {
            invRepo.insert(item)
            Log.d("Debug", item.heatNum)
        }
    }

    private fun confirmReception(items: List<RusalItem>) {
        val url = "$WEB_API/update/reception"

        val updateParams = RusalReceptionUpdateParams().getUpdateParamsList(items)

        val moshi : Moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, RusalReceptionUpdateParams::class.java)
        val adapter : JsonAdapter<List<RusalReceptionUpdateParams>> = moshi.adapter(listType)

        try {
            val postData = JSONArray(adapter.toJson(updateParams))

            val jsonArrayRequest = JsonArrayRequest(Request.Method.POST, url, postData, { response ->
                Log.d("DEBUG", response.toString())
            }, { error ->
                error.printStackTrace()
            })

            requestQueue.add(jsonArrayRequest)

        } catch (e : JSONException) {
            e.printStackTrace()
        }
    }

    // Returns false when update complete to signify loading as completed
    suspend fun initialize(invRepo: InventoryRepository, loading : MutableState<Boolean>) {
        retrieveInventory(object : VolleyCallBack {
            override fun onSuccess() {
                CoroutineScope(Dispatchers.IO).launch {
                    addToRepo(jsonResponse, invRepo)
                    loading.value = false
                }
            }
        })
    }

    fun initUpdate(items: List<RusalItem>, sessionType: SessionType) {
        CoroutineScope(Dispatchers.IO).launch {
            if (sessionType == SessionType.SHIPMENT) {
                confirmShipment(items)
            } else {
                confirmReception(items)
            }
        }
    }

}