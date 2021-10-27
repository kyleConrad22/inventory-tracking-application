package com.example.rusalqrandbarcodescanner.services.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.RequestFuture
import com.example.rusalqrandbarcodescanner.services.FileStorage
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class NewItemUploadWorker(context : Context, workerParams : WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {

        val dataFileName = inputData.getString(FileStorage.DATA_FILE_PATH)

        return try {
            val inputStream = applicationContext.openFileInput(dataFileName)

            try {
                if (inputStream != null) {
                    val postData = FileStorage.convertToJsonArray(inputStream)

                    val future: RequestFuture<JSONArray> = RequestFuture.newFuture()

                    val jsonArrayRequest = JsonArrayRequest(Request.Method.POST,
                        WEB_API_URL,
                        postData,
                        future,
                        future)

                    HttpRequestHandler.requestQueue.add(jsonArrayRequest)

                    try {
                        val response = future.get(30, TimeUnit.SECONDS)

                        applicationContext.deleteFile(dataFileName)

                        Result.success()

                    } catch (e: InterruptedException) {
                        Log.d(TAG, "Error occurred while upload new item", e)
                        Result.retry()

                    } catch (e: ExecutionException) {

                        // If output is empty and causes a ParseError then the call was successful
                        if (e.cause is ParseError) {
                            applicationContext.deleteFile(dataFileName)
                            Result.success()

                        } else {
                            Log.d(TAG, "Error occurred while upload new item", e)
                            Result.retry()
                        }

                    } catch (e: TimeoutException) {
                        Log.d(TAG, "Error occurred while uploading new item", e)
                        Result.retry()
                    }

                } else {
                    Log.e(TAG, "Error occurred uploading new item data - no valid filename")
                    Result.failure()
                }

            } catch (e: IOException) {
                Log.d(TAG, "Error occurred uploading new item data", e)
                Result.failure()

            } finally {
                inputStream.close()
            }
        } catch (e : JSONException) {
            Log.e(TAG, "Error occurred uploading new item data - JSON data could not be parsed")
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "NewItemUploadWorker"
        private const val WEB_API_URL = "http://45.22.122.47:8081/api/rusal/update/new"
    }

}