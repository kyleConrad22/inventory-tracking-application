package com.example.rusalqrandbarcodescanner.services

import android.content.Context
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.work.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.*
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.services.util.RusalReceptionUpdateParams
import com.example.rusalqrandbarcodescanner.services.util.RusalShipmentUpdateParams
import com.example.rusalqrandbarcodescanner.services.worker.NewItemUploadWorker
import com.example.rusalqrandbarcodescanner.services.worker.ReceptionUploadWorker
import com.example.rusalqrandbarcodescanner.services.worker.ShipmentUploadWorker
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object HttpRequestHandler {

    private const val WEB_API: String = "http://45.22.122.47:8081/api/rusal"
    lateinit var requestQueue: RequestQueue
    private var jsonResponse : String = ""

    private fun retrieveInventory(callBack : VolleyCallBack) {
        val stringRequest = StringRequest(Request.Method.GET, WEB_API, { response ->
                print(response.replace("\\n", ""))
                jsonResponse = response.replace("\\n", "")
                callBack.onSuccess()
        }, { error ->
                error.printStackTrace()
        })

        requestQueue.add(stringRequest)
        }

    private suspend fun confirmShipment(items: List<RusalItem>, context : Context) = withContext(Dispatchers.IO) {
        val uuidFileName = UUID.randomUUID().toString() + ".txt"

        val updateParams = RusalShipmentUpdateParams().getUpdateParamsList(items)

        val moshi : Moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, RusalShipmentUpdateParams::class.java)
        val adapter : JsonAdapter<List<RusalShipmentUpdateParams>> = moshi.adapter(listType)

        FileStorage.writeDataToFile(context, adapter.toJson(updateParams), uuidFileName)

        val oneTimeWorkRequest = createDefaultOneTimeWorkRequest<ShipmentUploadWorker>(fileName = uuidFileName)

        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
    }


    private fun createInventoryItems(items : List<RusalItem>, context : Context) {

        val uuidFileName = UUID.randomUUID().toString() + ".txt"

        val moshi : Moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, RusalItem::class.java)
        val adapter : JsonAdapter<List<RusalItem>> = moshi.adapter(listType)

        FileStorage.writeDataToFile(context, adapter.toJson(items), uuidFileName)

        val oneTimeWorkRequest = createDefaultOneTimeWorkRequest<NewItemUploadWorker>(fileName = uuidFileName)

        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)

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

    private fun confirmReception(items: List<RusalItem>, context : Context) {

        val uuidFileName = UUID.randomUUID().toString() + ".txt"

        val updateParams = RusalReceptionUpdateParams().getUpdateParamsList(items)

        val moshi : Moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, RusalReceptionUpdateParams::class.java)
        val adapter : JsonAdapter<List<RusalReceptionUpdateParams>> = moshi.adapter(listType)

        FileStorage.writeDataToFile(context, adapter.toJson(updateParams), uuidFileName)

        val oneTimeWorkRequest = createDefaultOneTimeWorkRequest<ReceptionUploadWorker>(fileName = uuidFileName)

        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)

    }

    private inline fun <reified T : ListenableWorker>createDefaultOneTimeWorkRequest(fileName : String) : OneTimeWorkRequest {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val data = Data.Builder()
            .putString(FileStorage.DATA_FILE_PATH, fileName)
            .build()

        return OneTimeWorkRequestBuilder<T>()
            .setConstraints(constraints)
            .setInputData(data)
            .build()
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

    // Initializes update to api given a list of items, creating new entries if the item was not found in the database
    fun initUpdate(items: List<RusalItem>, sessionType: SessionType, context : Context) = CoroutineScope(Dispatchers.IO).launch {
        val additionList = mutableListOf<RusalItem>()
        items.forEach { item ->
            if ('u' in item.barcode || 'n' in item.barcode) additionList.add(item)
        }
        if (additionList.isNotEmpty()) createInventoryItems(additionList, context)

        if (sessionType == SessionType.SHIPMENT) {
            confirmShipment(items, context)
        } else {
            confirmReception(items, context)
        }
    }

}