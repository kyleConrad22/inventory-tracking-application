package com.example.rusalqrandbarcodescanner.services

import android.util.Log
import androidx.lifecycle.Observer
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.viewmodels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.ReviewViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.ScannedCodeViewModel
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.*
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.stream.Collectors

object HttpRequestHandler {

    private var output: String = ""

    private suspend fun currentInventory() = withContext(Dispatchers.IO) {
            try {
                val url = URL("http", "45.22.122.47", 8081, "/api/rusal")

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
                                    .collect(Collectors.joining("\n"))
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

    private suspend fun addToRepo(invRepo : CurrentInventoryRepository) = withContext(Dispatchers.IO){
        currentInventory()
        val moshi : Moshi = Moshi.Builder().build()
        val listType = Types.newParameterizedType(List::class.java, CurrentInventoryLineItem::class.java)
        val adapter : JsonAdapter<List<CurrentInventoryLineItem>> = moshi.adapter(listType)

        val rusalItems = adapter.fromJson(output);

        for (item in rusalItems!!) {
            invRepo.insert(item)
            print(item.heatNum)
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