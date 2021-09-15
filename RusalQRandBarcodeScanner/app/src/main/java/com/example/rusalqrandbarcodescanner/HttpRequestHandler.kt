package com.example.rusalqrandbarcodescanner

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel
import kotlinx.coroutines.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.Buffer
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

object HttpRequestHandler {

    private var output: String = ""

    val loading = mutableStateOf(true)

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

    private suspend fun updateDatabase(viewModel: ScannedCodeViewModel, currentInventoryViewModel: CurrentInventoryViewModel) = withContext(Dispatchers.IO) {
        var codes = viewModel.allCodes.value
        val codeObserver = Observer<List<ScannedCode>>{ it ->
            codes = it
        }
        GlobalScope.launch(Dispatchers.Main) {
            viewModel.allCodes.observeForever(codeObserver)
            viewModel.allCodes.removeObserver(codeObserver)
        }
        if (codes != null) {
            for (code in codes!!) {

                    val heatNum = code.heatNum!!.replace("-", "")
                    val workOrder = code.workOrder!!
                    val loadNum = code.loadNum!!
                    val loader = code.loader!!.replace(" ", "_")
                    val loadTime = code.scanTime!!.replace(" ", "_")

                if (!code.barCode.contains('u')) {
                    try {
                        val url = URL("http",
                            "45.22.122.47",
                            8081,
                            "/demo/update?heatNum=$heatNum&workOrder=$workOrder&loadNum=$loadNum&loader=$loader&loadTime=$loadTime")
                        val urlConnection = url.openConnection() as HttpURLConnection

                        try {
                            val input: InputStream = BufferedInputStream(urlConnection.inputStream)
                            val result =
                                BufferedReader(InputStreamReader(input,
                                    StandardCharsets.UTF_8)).lines()
                                    .collect(Collectors.joining("\n"))
                            Log.d("DEBUG", result)
                        } finally {
                            urlConnection.disconnect()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    var uniCode = CurrentInventoryLineItem("","","","","","","","","","","","","","")
                    val uniObserver = Observer<CurrentInventoryLineItem?> { it ->
                        uniCode = it
                    }
                    GlobalScope.launch(Dispatchers.Main){
                        currentInventoryViewModel.findByBarcode(code.barCode).observeForever(uniObserver)
                        currentInventoryViewModel.findByBarcode(code.barCode).removeObserver(uniObserver)
                    }
                    val grossWeight = code.grossWgtKg!!
                    val netWeight = code.netWgtKg!!
                    val packageNum = code.packageNum!!
                    val dimension = uniCode.dimension
                    val grade = uniCode.grade
                    val certificateNum = uniCode.certificateNum
                    val quantity = uniCode.quantity
                    val barcode = code.barCode
                    val blNum = uniCode.blNum

                    try {
                        val url = URL("http",
                            "45.22.122.47",
                            8081,
                            "demo/add?heatNum=$heatNum&packageNum=$packageNum&grossWeightKg=$grossWeight&netWeightKg=$netWeight&quantity=$quantity&dimension=$dimension&grade=$grade&certificateNum=$certificateNum&blNum=$blNum&barcode=$barcode&workOrder=$workOrder&loadNum=$loadNum&loader=$loader&loadTime=$loadTime")

                        val urlConnection = url.openConnection() as HttpURLConnection

                        try {
                            val input: InputStream = BufferedInputStream(urlConnection.inputStream)
                            val result =
                                BufferedReader(InputStreamReader(input,
                                    StandardCharsets.UTF_8)).lines()
                                    .collect(Collectors.joining("\n"))
                            Log.d("DEBUG", result)
                        } finally {
                            urlConnection.disconnect()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
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
            var workOrder: String = ""
            var loadNum: String = ""
            var loader: String = ""
            var loadTime: String = ""

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
                    "workOrder" -> workOrder = value
                    "loadNum" -> loadNum = value
                    "loader" -> loader = value
                    "loadTime" -> loadTime = value
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
                barcode = barcode,
                workOrder = workOrder,
                loadNum = loadNum,
                loader = loader,
                loadTime = loadTime
            ))
        }
    }

    fun initialize(viewModel: CurrentInventoryViewModel) {
        CoroutineScope(Dispatchers.IO).launch {
            addToRepo(viewModel = viewModel)
        }
    }

    fun initUpdate(viewModel: ScannedCodeViewModel, currentInventoryViewModel: CurrentInventoryViewModel) {
        CoroutineScope(Dispatchers.IO).launch {
            updateDatabase(viewModel = viewModel, currentInventoryViewModel = currentInventoryViewModel)
        }
    }

}