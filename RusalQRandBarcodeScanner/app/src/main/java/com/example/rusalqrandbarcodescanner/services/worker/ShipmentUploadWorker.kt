package com.example.rusalqrandbarcodescanner.services.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.example.rusalqrandbarcodescanner.services.FileStorage
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import org.json.JSONObject
import java.lang.Exception
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ShipmentUploadWorker(context : Context, workerParams : WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {

        val inputStream = applicationContext.openFileInput(DATA_FILE)

        return try {
            if (inputStream != null) {

                val postData = FileStorage.convertToJsonObject(inputStream)

                val future : RequestFuture<JSONObject> = RequestFuture.newFuture()

                val jsonObjectRequest = JsonObjectRequest(Request.Method.POST, WEB_API_URL, postData, future, future)

                HttpRequestHandler.requestQueue.add(jsonObjectRequest)

                try {
                    val response = future.get(30, TimeUnit.SECONDS)
                    Log.e(TAG, response.toString())
                    Result.success()
                } catch (e : InterruptedException) {
                    Log.d(TAG, "Error occurred uploading shipment data", e)
                    Result.retry()
                } catch (e : ExecutionException) {
                    Log.d(TAG, "Error occurred uploading shipment data", e)
                    Result.retry()
                } catch (e : TimeoutException) {
                    Log.d(TAG, "Error occurred uploading shipment data", e)
                    Result.retry()
                }

            } else {
                Log.e(TAG, "Error occurred uploading shipment data - no valid filename")
                Result.failure()
            }
        } catch (e : Exception) {
            Log.d(TAG, "Error occurred uploading shipment data", e)
            Result.failure()
        }
    }

    companion object {
        private val TAG = "ShipmentUploadWorker"
        private val DATA_FILE = "data.txt"
        private val WEB_API_URL = "http://45.22.122.47:8081/api/rusal/update/shipment"
    }
}