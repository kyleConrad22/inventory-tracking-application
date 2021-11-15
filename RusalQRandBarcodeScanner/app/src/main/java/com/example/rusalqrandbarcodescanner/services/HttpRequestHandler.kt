package com.example.rusalqrandbarcodescanner.services

import android.content.Context
import android.util.Log
import androidx.work.*
import com.android.volley.RequestQueue
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.services.util.RusalReceptionUpdateParams
import com.example.rusalqrandbarcodescanner.services.util.RusalShipmentUpdateParams
import com.example.rusalqrandbarcodescanner.services.worker.DownloadWorker
import com.example.rusalqrandbarcodescanner.services.worker.NewItemUploadWorker
import com.example.rusalqrandbarcodescanner.services.worker.ReceptionUploadWorker
import com.example.rusalqrandbarcodescanner.services.worker.ShipmentUploadWorker
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.*
import java.io.IOException
import java.util.*

object HttpRequestHandler {

    private const val TAG = "HttpRequestHandler"
    lateinit var requestQueue: RequestQueue
    lateinit var repo : InventoryRepository

    @Suppress("BlockingMethodInNonBlockingContext")
    @Throws(IOException::class)
    suspend fun updateLocalDatabase(response : String) = withContext(Dispatchers.IO) {
        val moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, RusalItem::class.java)
        val adapter : JsonAdapter<List<RusalItem>> = moshi.adapter(listType)

        val items = adapter.fromJson(response)

        items?.forEach { item ->
            repo.insert(item)
            Log.d(TAG, item.heatNum)
        } ?: Log.e(TAG, "Response received was parsed as null")

    }

    private suspend fun confirmShipment(items: List<RusalItem>, context : Context) = withContext(Dispatchers.IO) {
        val uuidFileName = UUID.randomUUID().toString() + ".txt"

        val updateParams = RusalShipmentUpdateParams().getUpdateParamsList(items)

        val moshi : Moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, RusalShipmentUpdateParams::class.java)
        val adapter : JsonAdapter<List<RusalShipmentUpdateParams>> = moshi.adapter(listType)

        FileStorage.writeDataToFile(context, adapter.toJson(updateParams), uuidFileName)

        val oneTimeWorkRequest = createDefaultOneTimeWorkRequestUpload<ShipmentUploadWorker>(fileName = uuidFileName)

        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
    }


    private fun createInventoryItems(items : List<RusalItem>, context : Context) {

        val uuidFileName = UUID.randomUUID().toString() + ".txt"

        val moshi : Moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, RusalItem::class.java)
        val adapter : JsonAdapter<List<RusalItem>> = moshi.adapter(listType)

        FileStorage.writeDataToFile(context, adapter.toJson(items), uuidFileName)

        val oneTimeWorkRequest = createDefaultOneTimeWorkRequestUpload<NewItemUploadWorker>(fileName = uuidFileName)

        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)

    }

    private fun confirmReception(items: List<RusalItem>, context : Context) {

        val uuidFileName = UUID.randomUUID().toString() + ".txt"

        val updateParams = RusalReceptionUpdateParams().getUpdateParamsList(items)

        val moshi : Moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, RusalReceptionUpdateParams::class.java)
        val adapter : JsonAdapter<List<RusalReceptionUpdateParams>> = moshi.adapter(listType)

        FileStorage.writeDataToFile(context, adapter.toJson(updateParams), uuidFileName)

        val oneTimeWorkRequest = createDefaultOneTimeWorkRequestUpload<ReceptionUploadWorker>(fileName = uuidFileName)

        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)

    }

    // To be used in ViewModel to initiate sync with Web API, returns the UUID of the initiated worker
    fun startLocalDatabaseSync(context : Context) : UUID {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<DownloadWorker>()
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork("LocalDatabaseSync", ExistingWorkPolicy.REPLACE, workRequest)

        return workRequest.id
    }

    private inline fun <reified T : ListenableWorker>createDefaultOneTimeWorkRequestUpload(fileName : String) : OneTimeWorkRequest {
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