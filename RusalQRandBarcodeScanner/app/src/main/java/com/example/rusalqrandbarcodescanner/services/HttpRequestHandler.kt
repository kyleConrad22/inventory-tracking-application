@file:Suppress("BlockingMethodInNonBlockingContext")

package com.example.rusalqrandbarcodescanner.services

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
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

    private const val WEB_API: String = "http://45.22.133.47:8081/api/rusal"

    private suspend fun retrieveInventory(context: Context, invRepo: InventoryRepository) =
        withContext(Dispatchers.IO) {
            val requestQueue: RequestQueue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(Request.Method.GET, WEB_API, {
                @Override
                fun onResponse(response: String) {
                    CoroutineScope(Dispatchers.IO).launch {
                        val value = CoroutineScope(Dispatchers.IO).async {
                            addToRepo(response, invRepo)
                        }
                        value.await()
                    }
                }
            }, {
                @Override
                fun onErrorResponse(error: VolleyError) {
                    error.printStackTrace()
                }
            })

            requestQueue.add(stringRequest)
        }

    private suspend fun confirmShipment(context: Context, items: List<RusalItem>) =
        withContext(Dispatchers.IO) {
            for (item in items) {

                if (!item.barcode.contains('u')) {
                    updateDatabaseForShipment(context, item)
                } else {
                    createInventoryItem(context, item)
                }
            }
        }


    private fun updateDatabaseForShipment(context : Context, item : RusalItem) {
        val url = "$WEB_API/update"
        val requestQueue : RequestQueue = Volley.newRequestQueue(context)

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

        val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, url, postData, {
            @Override
            fun onResponse(response : String) {
                print(response)
            }
        }, {
            @Override
            fun onErrorResponse(error : VolleyError) {
                error.printStackTrace()
            }
        })

        requestQueue.add(jsonObjectRequest)
    }

    private fun createInventoryItem(context : Context, item : RusalItem) {

        val moshi : Moshi = Moshi.Builder().build()
        val adapter : JsonAdapter<RusalItem> = moshi.adapter(RusalItem::class.java)

        try {
            val postData = JSONObject(adapter.toJson(item))

            val requestQueue : RequestQueue = Volley.newRequestQueue(context)

            val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, WEB_API, postData, {
                @Override
                fun onResponse(response : String) {
                    print(response)
                }
            }, {
                fun onErrorResponse(error : VolleyError) {
                    error.printStackTrace()
                }
            })

            requestQueue.add(jsonObjectRequest)

        } catch (e : JSONException) {
            e.printStackTrace()
        }

    }

    private suspend fun addToRepo(response : String, invRepo : InventoryRepository) = withContext(Dispatchers.IO){
        val moshi : Moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, RusalItem::class.java)
        val adapter : JsonAdapter<List<RusalItem>> = moshi.adapter(listType)

        val rusalItems = adapter.fromJson(response);

        for (item in rusalItems!!) {
            invRepo.insert(item)
            print(item.heatNum)
        }
    }

    /* TODO */
    private fun confirmReception(context : Context, items: List<RusalItem>) {
        val url = "$WEB_API/update/reception"

        val updateParams = RusalReceptionUpdateParams().getUpdateParamsList(items)

        val moshi : Moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, RusalReceptionUpdateParams::class.java)
        val adapter : JsonAdapter<List<RusalReceptionUpdateParams>> = moshi.adapter(listType)

        try {
            val postData = JSONArray(adapter.toJson(updateParams))

            val requestQueue : RequestQueue = Volley.newRequestQueue(context)

            val jsonArrayRequest = JsonArrayRequest(Request.Method.POST, url, postData, {
                @Override
                fun onResponse(response : String) {
                    print(response)
                }
            }, {
                @Override
                fun onErrorResponse(error : VolleyError) {
                    error.printStackTrace()
                }
            })

            requestQueue.add(jsonArrayRequest)

        } catch (e : JSONException) {
            e.printStackTrace()
        }
    }

    // Returns false when update complete to signify loading as completed
    suspend fun initialize(context : Context, invRepo: InventoryRepository): Boolean {
        retrieveInventory(context, invRepo)
        return false
    }

    fun initUpdate(context : Context, items: List<RusalItem>, sessionType: SessionType) {
        CoroutineScope(Dispatchers.IO).launch {
            if (sessionType == SessionType.SHIPMENT) {
                confirmShipment(context, items)
            } else {
                confirmReception(context, items)
            }
        }
    }

}