package com.example.rusalqrandbarcodescanner.services

import android.content.Context
import android.util.Log
import org.json.JSONArray
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.OutputStreamWriter
import java.lang.StringBuilder

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

            return JSONArray(stringBuilder.toString())
        }
    }

}