package com.example.rusalqrandbarcodescanner.services

import android.content.Context
import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter
import java.lang.StringBuilder
import java.nio.Buffer

object FileStorage {

    private const val TAG = "FileStorage"

    fun writeDataToFile(context : Context, fileBody : String) {

        try {
            val outputStreamWriter = OutputStreamWriter(context.openFileOutput("data.txt", Context.MODE_PRIVATE))
            outputStreamWriter.write(fileBody)
            outputStreamWriter.close()

        } catch (e: IOException) {
            Log.d(TAG, "Error writing to file", e)
        }
    }

    fun convertToJsonArray(inputStream: InputStream) : JSONArray {
        BufferedReader(inputStream.reader()).use { bufferedReader ->

            val stringBuilder = StringBuilder()

            bufferedReader.forEachLine { line ->
                stringBuilder.append(line)
            }

            return try {
                JSONArray(stringBuilder.toString())
            } catch (e : JSONException) {
                Log.d(TAG, "Error occurred while converting stored data to JSONArray", e)
                JSONArray()
            }
        }
    }

    fun convertToJsonObject(inputStream : InputStream) : JSONObject {
        BufferedReader(inputStream.reader()).use { bufferedReader ->

            val stringBuilder = StringBuilder()

            bufferedReader.forEachLine { line ->
                stringBuilder.append(line)
            }

            return try {
                JSONObject(stringBuilder.toString())
            } catch (e : JSONException) {
                Log.d(TAG, "Error occurred while converting stored data to JSONObject", e)
                JSONObject()
            }
        }
    }

}