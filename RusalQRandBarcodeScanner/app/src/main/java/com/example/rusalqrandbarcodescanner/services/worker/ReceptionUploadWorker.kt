package com.example.rusalqrandbarcodescanner.services.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.android.volley.ParseError
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.RequestFuture
import com.example.rusalqrandbarcodescanner.services.FileStorage
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import org.json.JSONArray
import java.io.FileNotFoundException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ReceptionUploadWorker(context : Context, workerParams : WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {

        val dataFileName = inputData.getString(FileStorage.DATA_FILE_PATH)

        return try {
            val inputStream = applicationContext.openFileInput(dataFileName)

            try {
                if (inputStream != null) {

                    val postData = FileStorage.convertToJsonArray(inputStream)

                    val future: RequestFuture<JSONArray> = RequestFuture.newFuture()

                    val jsonArrayRequest = JsonArrayRequest(Request.Method.POST, WEB_API_URL, postData, future, future)

                    HttpRequestHandler.requestQueue.add(jsonArrayRequest)

                    try {
                        val response = future.get(30, TimeUnit.SECONDS)

                        applicationContext.deleteFile(dataFileName)

                        Result.success()

                    } catch (e: InterruptedException) {
                        Log.d(TAG, "Error uploading reception data", e)
                        Result.retry()

                    } catch (e: ExecutionException) {

                        // If output is empty and causes a ParseError then the call was successful
                        if (e.cause is ParseError) {
                            applicationContext.deleteFile(dataFileName)
                            Result.success()
                        } else {
                            Log.d(TAG, "Error uploading reception data", e)
                            Result.retry()
                        }

                    } catch (e: TimeoutException) {
                        Log.d(TAG, "Error uploading reception data", e)
                        Result.retry()
                    }

                } else {
                    Log.e(TAG, "Error uploading reception data - no valid filename")
                    Result.failure()
                }

            } catch (e: Exception) {
                Log.d(TAG, "Error uploading reception data", e)
                Result.failure()

            } finally {
                inputStream.close()
            }

        } catch (e : FileNotFoundException) {
            Log.e(TAG, "Error uploading reception data - data file could not be found", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "ReceptionUploadWorker"
        private const val WEB_API_URL: String = "http://172.78.63.188:8081/api/rusal/update/reception"
    }
}