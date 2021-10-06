package com.example.rusalqrandbarcodescanner.services

import android.util.Log
import androidx.lifecycle.Observer
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.viewmodels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.ReviewViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.ScannedCodeViewModel
import kotlinx.coroutines.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.stream.Collector
import java.util.stream.Collectors
import java.util.stream.Collectors.joining
import java.util.stream.Collectors.toMap

class HttpRequestHandler {

    lateinit var urlConnection: HttpURLConnection

    // Attempts to connect to URL supplied by parameter, if connection is made then returns true and sets urlConnection; otherwise returns false
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun connectToUrl(url : URL) : Boolean = withContext(Dispatchers.IO) {
        try {
            urlConnection = url.openConnection() as HttpURLConnection

            try {
                urlConnection.connect()
                return@withContext urlConnection.responseCode == HttpURLConnection.HTTP_OK
            } catch (e: Exception) {
                e.printStackTrace()
                urlConnection.disconnect()
                return@withContext false
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext false
        }
    }

    fun getApiResponse() : String {
        try {
            val input : InputStream = BufferedInputStream(urlConnection.inputStream)
             return BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).lines()
                 .collect(joining("\n"))
        } finally {
            urlConnection.disconnect()
        }
    }

    fun parseApiResponse(response : String) : List<HashMap<String, String>> {
        val result = mutableListOf<HashMap<String, String>>()

        Regex("\\{.+}").findAll(response).forEach { item ->
            result.add(HashMap())
            Regex("\".*\":\".*\"").findAll(item.value).forEach { pair ->
                val keyValue = pair.value.replace("\"", "").split(":")
                result.last()[keyValue[0]] = keyValue[1]
            }
        }
        return result.toList()
    }

    private suspend fun currentInventory() = withContext(Dispatchers.IO) {
            try {
                val url = URL("http", "45.22.122.47", 8081, "/api/rusal")

                val urlConnection = url.openConnection() as HttpURLConnection

                try {
                    val input: InputStream = BufferedInputStream(urlConnection.inputStream)
                    val output = BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).lines()
                            .collect(joining("\n"))
                    Log.d("DEBUG",
                        BufferedReader(InputStreamReader(input, StandardCharsets.UTF_8)).lines()
                            .collect(joining("\n")))
                } finally {
                    urlConnection.disconnect()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

    private suspend fun updateDatabase(reviewViewModel : ReviewViewModel, viewModel: ScannedCodeViewModel, currentInventoryViewModel: CurrentInventoryViewModel) = withContext(Dispatchers.IO) {
        val codes = viewModel.allCodes.value

        if (codes != null) {
            for (code in codes) {

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
                            "/api/rusal/update?heatNum=$heatNum&workOrder=$workOrder&loadNum=$loadNum&loader=$loader&loadTime=$loadTime")
                        val urlConnection = url.openConnection() as HttpURLConnection

                        try {
                            val input: InputStream = BufferedInputStream(urlConnection.inputStream)
                            val result =
                                BufferedReader(InputStreamReader(input,
                                    StandardCharsets.UTF_8)).lines()
                                    .collect(joining("\n"))
                            Log.d("DEBUG", result)
                        } finally {
                            urlConnection.disconnect()
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } else {
                    var newCode = CurrentInventoryLineItem("","","","","","","","","","","","","","")
                    val uniObserver = Observer<CurrentInventoryLineItem?> { it ->
                        newCode = it
                    }
                    GlobalScope.launch(Dispatchers.Main){
                        currentInventoryViewModel.findByBarcode(code.barCode).observeForever(uniObserver)
                        currentInventoryViewModel.findByBarcode(code.barCode).removeObserver(uniObserver)
                    }
                    val grossWeight = code.grossWgtKg!!
                    val netWeight = code.netWgtKg!!
                    val packageNum = code.packageNum!!
                    val dimension = newCode.dimension
                    val grade = newCode.grade
                    val certificateNum = newCode.certificateNum
                    val quantity = newCode.quantity
                    val barcode = code.barCode
                    val blNum = newCode.blNum

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
                                    .collect(joining("\n"))
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

    private suspend fun addToRepo(invRepo : CurrentInventoryRepository) = withContext(Dispatchers.IO){
        currentInventory()
        val output = ""
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
                val fieldValClean = fieldVal.replace("\"", "").replace("\\n","")
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

            invRepo.insert(CurrentInventoryLineItem(
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

    suspend fun initialize(invRepo: CurrentInventoryRepository) : Boolean {
        val value = CoroutineScope(Dispatchers.IO).async {
            addToRepo(invRepo)
        }
        value.await()
        var loading = false
        return loading
    }

    fun initUpdate(reviewViewModel: ReviewViewModel, viewModel: ScannedCodeViewModel, currentInventoryViewModel: CurrentInventoryViewModel) {
        CoroutineScope(Dispatchers.IO).launch {
            updateDatabase(reviewViewModel = reviewViewModel, viewModel = viewModel, currentInventoryViewModel = currentInventoryViewModel)
        }
    }

}