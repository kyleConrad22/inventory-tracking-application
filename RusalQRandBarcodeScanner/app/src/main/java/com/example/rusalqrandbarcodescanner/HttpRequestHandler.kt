package com.example.rusalqrandbarcodescanner

import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import kotlinx.coroutines.*
import java.io.*
import java.lang.NullPointerException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

object HttpRequestHandler {

    private var output: String = ""

    private suspend fun currentInventory() = withContext(Dispatchers.IO) {
            try {
                val url = URL("http", "45.22.122.47", 8081, "/demo/all")

                val urlConnection = url.openConnection() as HttpURLConnection

                try {
                    val input: InputStream = BufferedInputStream(urlConnection.inputStream)
                    output = BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).lines()
                            .collect(Collectors.joining("\n"))
                    Log.d("DEBUG",
                        BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).lines()
                            .collect(Collectors.joining("\n")))
                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    private suspend fun addToRepo(viewModel: CurrentInventoryViewModel) = withContext(Dispatchers.IO){
        viewModel.deleteAll()
        currentInventory()
        val lines = output.split("},{").toTypedArray()
        lines[0] = lines[0].replace("[{", "")
        lines[lines.size - 1] = lines[lines.size - 1].replace("}]", "")

        var iterable = 0

        for (line in lines) {
            val lineFields = line.split(",").toTypedArray()
            var heatNum: String = ""
            var packageNum: String = ""
            var grossWeightKg: String = ""
            var netWeightKg: String = ""
            var quantity: String = ""
            var dimension: String = ""
            var grade: String = ""
            var certificateNum: String = ""
            var blNum: String = ""
            var barcode: String = ""

            for (fieldVal in lineFields) {
                val fieldValClean = fieldVal.replace("\"", "")
                val field = fieldValClean.split(":")[0]
                val value = fieldValClean.split(":")[1]
                when (field) {
                    "heatNum" -> heatNum = value
                    "packageNum" -> packageNum = value
                    "grossWeightKg" -> grossWeightKg = value
                    "netWeightKg" -> netWeightKg = value
                    "quantity" -> quantity = value
                    "dimension" -> dimension = value
                    "grade" -> grade = value
                    "certificateNum" -> certificateNum = value
                    "blNum" -> blNum = value
                    "barcode" -> barcode = value
                    else -> {
                        Log.d("ERROR", "The field $field, with value $value does not match any predefined records")
                    }
                }
            }
            iterable++
            Log.d("DEBUG", iterable.toString())

            viewModel.insert(CurrentInventoryLineItem(
                heatNum = heatNum,
                packageNum = packageNum,
                grossWeightKg = grossWeightKg,
                netWeightKg = netWeightKg,
                quantity = quantity,
                dimension = dimension,
                grade = grade,
                certificateNum = certificateNum,
                blNum = blNum,
                barcode = barcode
            ))
        }
    }

    fun initialize(viewModel: CurrentInventoryViewModel) {
        CoroutineScope(Dispatchers.IO).launch {
            addToRepo(viewModel = viewModel)
        }
    }

}