package com.example.rusalqrandbarcodescanner.viewModels

import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.ScannedInfo
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ManualEntryViewModel(private val userRepository : UserInputRepository, private val codeRepository : CodeRepository, private val inventoryRepository : CurrentInventoryRepository) : ViewModel() {
    private val currentInput = userRepository.currentInput.asLiveData()
    val heat: MutableLiveData<String> = MutableLiveData("")

    private fun setTime(): String{
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM_dd_yyyy HH:mm:ss")
        val timeNow: LocalDateTime = LocalDateTime.now()
        return formatter.format(timeNow)
    }

    val destination = mutableStateOf("")

    private val triggerLoader = MutableLiveData<Unit>()

    fun refresh() {
        triggerLoader.value = Unit
    }

    val isSearchVis = mutableStateOf(false)

    val loading = mutableStateOf(false)

    val isBaseHeat = triggerLoader.switchMap { isBaseHeatLogic() }

    private fun isBaseHeatLogic() : LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()
        mediatorLiveData.addSource(heat) { it ->
            mediatorLiveData.removeSource(heat)
            if (it.length in 6..9) {
                mediatorLiveData.value = it.length == 6
                isSearchVis.value = true
            } else {
                mediatorLiveData.value = false
                isSearchVis.value = false
            }
        }
        return mediatorLiveData
    }

    // Makes synchronous call for database query of returning line items by their base heat
    private suspend fun findByBaseHeat(heat: String): List<CurrentInventoryLineItem>? {
        var result : List<CurrentInventoryLineItem>? = null
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
            result = inventoryRepository.findByBaseHeat("%$heat%")
            }
        }
        println(value.await())
        return result
    }

    fun saveHeatInput() = viewModelScope.launch {
        var userInput : UserInput? = null
        if (currentInput.value != null) {
            val input = currentInput.value!![0]
            userInput = UserInput(
                id = "data",
                load = input.load,
                order = input.order,
                loader = input.loader,
                checker = input.checker,
                vessel = input.vessel,
                bl = input.bl,
                bundleQuantity = input.bundleQuantity,
                heatNum = heat.value,
                pieceCount = input.pieceCount,
                type = input.type
            )
        } else {
            val mediatorLiveData = MediatorLiveData<UserInput>()
            mediatorLiveData.addSource(currentInput) { it ->
                if (!it.isNullOrEmpty()) {
                    mediatorLiveData.removeSource(currentInput)
                    val input = it[0]
                    userInput = UserInput(
                        id = "data",
                        load = input.load,
                        order = input.order,
                        loader = input.loader,
                        checker = input.checker,
                        vessel = input.vessel,
                        bl = input.bl,
                        bundleQuantity = input.bundleQuantity,
                        heatNum = heat.value,
                        pieceCount = input.pieceCount,
                        type = input.type
                    )
                }
            }
        }
        if (userInput != null) {
            userRepository.update(userInput!!)
        }
    }

    fun insert(lineItem: CurrentInventoryLineItem) = viewModelScope.launch {
        inventoryRepository.insert(lineItem)
    }

    /*
    private suspend fun addNewItemByBaseHeat() {
        loading.value = true
        val lineItem = newItemByBaseHeat()
        val mediatorLiveData = MediatorLiveData<Boolean>()
        mediatorLiveData.addSource(lineItem) {
            mediatorLiveData.removeSource(lineItem)
            mediatorLiveData.value = it.blNum != "N/A"

            ScannedInfo.getValues(it)
            insert(it)
        }
    }

    private suspend fun newItemByBaseHeat(): LiveData<CurrentInventoryLineItem>{
        val listByHeat = findByBaseHeat("%${heat.value}%")
        val mediatorLiveData = MediatorLiveData<CurrentInventoryLineItem>()
        mediatorLiveData.addSource(listByHeat) {byHeat ->
            mediatorLiveData.removeSource(listByHeat)

            Log.d("DEBUG", "made it here")
            if (byHeat != null) {
                val listByBarcode = findByBarcodes("%${heat.value}u%")
                mediatorLiveData.addSource(listByBarcode) { byBarcode ->
                    mediatorLiveData.removeSource(listByBarcode)

                    val blList = blList.value
                    val quantList = quantityList.value

                    val barcode = if (byBarcode == null) { "${heat.value}u1" } else { "${heat.value}u${byBarcode.size + 1}" }
                    Log.d("DEBUG",  barcode)
                    val bl = blList!![0]
                    val quantity = quantList!![0]
                    val dimension = byHeat[0].dimension
                    val grade = byHeat[0].grade
                    val certificateNum = byHeat[0].certificateNum

                    mediatorLiveData.value = CurrentInventoryLineItem(
                        heatNum = heat.value!!,
                        packageNum = "N/A",
                        grossWeightKg = "N/A",
                        netWeightKg = "N/A",
                        quantity = quantity,
                        dimension = dimension,
                        grade = grade,
                        certificateNum = certificateNum,
                        blNum = bl,
                        barcode = barcode,
                        workOrder = currentInput.value!![0].order,
                        loadNum = currentInput.value!![0].load,
                        loader = currentInput.value!![0].loader,
                        loadTime = setTime()
                    )
                }
            } else {
                mediatorLiveData.value =
                    CurrentInventoryLineItem(
                        heatNum = heat.value!!,
                        packageNum = "N/A",
                        grossWeightKg = "N/A",
                        netWeightKg = "N/A",
                        quantity = "N/A",
                        dimension = "N/A",
                        grade = "N/A",
                        certificateNum = "N/A",
                        blNum = "N/A",
                        barcode = "N/A",
                        workOrder = currentInput.value!![0].order,
                        loadNum = currentInput.value!![0].load,
                        loader = currentInput.value!![0].loader,
                        loadTime = setTime()
                    )
            }
        }
        return mediatorLiveData
    }
     */


    // Returns destination for NavController
    fun setDestination() {
        loading.value = true

        GlobalScope.launch(Dispatchers.Main) { // Launches coroutine in main thread
            setDestinationLogic()
        }
    }

    // Encapsulates methods to get destination for synchronous calls
    private suspend fun setDestinationLogic() {
        val repoData = findByBaseHeat(heat.value!!)

        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                val blList = getBlList(repoData)
                val quantityList = getQuantityList(repoData)

                destination.value =
                    when {
                        blList.isNullOrEmpty() || quantityList.isNullOrEmpty() -> "N/A"
                        blList.size == 1 && quantityList.size == 1 -> Screen.ScannedInfoScreen.title
                        blList.size > 1 && quantityList.size == 1 -> Screen.BlOptionsScreen.title
                        blList.size == 1 && quantityList.size > 1 -> Screen.QuantityOptionsScreen.title
                        blList.size > 1 && quantityList.size > 1 -> Screen.ToBeImplementedScreen.title
                        else -> "N/A"
                    }
                if (destination.value != "N/A"){
                    saveHeatInput()
                }
                if (destination.value == Screen.ScannedInfoScreen.title) {
                    //addNewItemByBaseHeat()
                }
            }
        }
        println(value.await())
        loading.value = false
    }
    // Returns list of all BLs found for given heat number, by base heat only
    private fun getBlList(repoData : List<CurrentInventoryLineItem>?) : List<String> {
        val blList = mutableListOf<String>()
        if (!repoData.isNullOrEmpty()) {
            for (lineItem in repoData) {
                if (blList.find { it == lineItem.blNum } == null) {
                    Log.d("DEBUG", lineItem.blNum.toString())
                    blList.add(lineItem.blNum!!)
                }
            }
        }
        return blList
    }

    // Returns list of all BLs found for given heat number, by base heat only
    private fun getQuantityList(repoData : List<CurrentInventoryLineItem>?) : List<String> {
        val quantityList = mutableListOf<String>()
        if (!repoData.isNullOrEmpty()) {
            for (lineItem in repoData) {
                if (quantityList.find { it == lineItem.quantity } == null) {
                    Log.d("DEBUG", lineItem.quantity.toString())
                    quantityList.add(lineItem.quantity!!)
                }
            }
        }
        return quantityList
    }

    private fun findByBarcodes(barcode: String) : LiveData<List<CurrentInventoryLineItem>?> {
        val result = MutableLiveData<List<CurrentInventoryLineItem>?>()
        viewModelScope.launch {
            val codes = inventoryRepository.findByBarcodes(barcode)
            result.postValue(codes)
        }
        return result
    }

    class ManualEntryViewModelFactory(private val userRepository : UserInputRepository, private val codeRepository : CodeRepository, private val inventoryRepository : CurrentInventoryRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(ManualEntryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ManualEntryViewModel(userRepository, codeRepository, inventoryRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}