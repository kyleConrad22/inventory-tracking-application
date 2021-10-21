package com.example.rusalqrandbarcodescanner.services.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.example.rusalqrandbarcodescanner.services.FileStorage
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ShipmentUploadWorker(context : Context, workerParams : WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {

        val dataFileName = inputData.getString(FileStorage.DATA_FILE_PATH)

        return try {
            val inputStream = applicationContext.openFileInput(dataFileName)

            try {
                if (inputStream != null) {

                    val postData = FileStorage.convertToJsonObject(inputStream)

                    val future: RequestFuture<JSONObject> = RequestFuture.newFuture()

                    val jsonObjectRequest = JsonObjectRequest(Request.Method.POST,
                        WEB_API_URL,
                        postData,
                        future,
                        future)

                    HttpRequestHandler.requestQueue.add(jsonObjectRequest)

                    try {
                        val response = future.get(30, TimeUnit.SECONDS)

                        applicationContext.deleteFile(dataFileName)

                        Result.success()

                    } catch (e: InterruptedException) {
                        Log.d(TAG, "Error occurred uploading shipment data", e)
                        Result.retry()

                    } catch (e: ExecutionException) {

                        // If output is empty and causes a ParseError then the call was successful
                        if (e.cause is ParseError) {
                            applicationContext.deleteFile(dataFileName)
                            Result.success()

                        } else {
                            Log.d(TAG, "Error occurred uploading shipment data", e)
                            Result.retry()
                        }

                    } catch (e: TimeoutException) {
                        Log.d(TAG, "Error occurred uploading shipment data", e)
                        Result.retry()
                    }

                } else {
                    Log.e(TAG, "Error occurred uploading shipment data - no valid filename")
                    Result.failure()
                }

            } catch (e: Exception) {
                Log.d(TAG, "Error occurred uploading shipment data", e)
                Result.failure()
            }

        } catch (e : JSONException) {
            Log.e(TAG, "Error occurred uploading shipment data - JSON data could not be parsed")
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "ShipmentUploadWorker"
        private const val WEB_API_URL = "http://45.22.122.47:8081/api/rusal/update/shipment"
    }
}