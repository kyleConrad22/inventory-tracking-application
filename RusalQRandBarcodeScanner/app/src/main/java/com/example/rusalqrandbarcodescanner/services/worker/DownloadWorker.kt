package com.example.rusalqrandbarcodescanner.services.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.RequestFuture
import com.android.volley.toolbox.StringRequest
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@Suppress("BlockingMethodInNonBlockingContext")
class DownloadWorker(context : Context, parameters : WorkerParameters) : CoroutineWorker(context, parameters) {

    override suspend fun doWork() : Result = withContext(Dispatchers.IO) {

        val future : RequestFuture<String> = RequestFuture.newFuture()
        val stringRequest  = StringRequest(Request.Method.GET, WEB_API_URL, future, future)

        HttpRequestHandler.requestQueue.add(stringRequest)

        return@withContext try {
            val response = future.get(30, TimeUnit.SECONDS)

            HttpRequestHandler.updateLocalDatabase(response)

            Result.success()

        } catch (e : InterruptedException) {
            Log.d(TAG, "Error occurred while retrieving data from API", e )
            Result.retry()

        } catch (e : ExecutionException) {
            Log.d(TAG, "Error occurred while retrieving data from API", e)
            if (e.cause is VolleyError) Result.failure()
            else Result.retry()

        } catch (e : TimeoutException) {
            Log.d(TAG, "Error occurred while retrieving data from API", e)
            Result.retry()
        } catch (e : IOException) {
            Log.d(TAG, "Error occurred while parsing data from API", e)
            Result.failure()
        }
    }


    companion object {
        private const val TAG = "DownloadWorker"
        private const val WEB_API_URL = "http://172.78.63.188:8081/api/rusal"
    }
}