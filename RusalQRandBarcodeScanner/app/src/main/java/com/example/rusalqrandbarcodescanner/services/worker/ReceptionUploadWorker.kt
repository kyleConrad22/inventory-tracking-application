package com.example.rusalqrandbarcodescanner.services.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.RequestFuture
import com.example.rusalqrandbarcodescanner.services.FileStorage
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import org.json.JSONArray
import org.json.JSONException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ReceptionUploadWorker(context : Context, workerParams : WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {

        val inputStream = applicationContext.openFileInput(DATA_FILE)
        return try {

            if (inputStream != null) {

                try {

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
                        Log.e(TAG, response.toString())
                        Result.success()
                    } catch (e: InterruptedException) {
                        Log.d(TAG, "Error uploading reception data", e)
                        Result.retry()
                    } catch (e: ExecutionException) {
                        Log.d(TAG, "Error uploading reception data", e)
                        Result.retry()
                    } catch (e: TimeoutException) {
                        Log.d(TAG, "Error uploading reception data", e)
                        Result.retry()
                    }
                } catch (e : JSONException) {
                    Log.d(TAG, "Error uploading reception data - could not parse json request", e)
                    Result.failure()
                }

            } else {
                Log.e(TAG, "Error uploading reception data - no valid filename")
                Result.failure()
            }

            Result.success()

        } catch (e : Exception) {
            Log.d(TAG, "Error uploading reception data", e)
            Result.failure()
        } finally {
            inputStream.close()
        }
    }

    companion object {
        private const val TAG = "ReceptionUploadWorker"
        private const val DATA_FILE = "data.txt"
        private const val WEB_API_URL: String = "http://45.22.122.47:8081/api/rusal/update/reception"
    }
}