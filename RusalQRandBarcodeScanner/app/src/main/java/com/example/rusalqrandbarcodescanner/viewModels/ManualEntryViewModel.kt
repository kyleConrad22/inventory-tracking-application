package com.example.rusalqrandbarcodescanner.viewModels

import android.provider.MediaStore
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.ScannedInfo
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ManualEntryViewModel(private val userRepository : UserInputRepository, private val codeRepository : CodeRepository, private val inventoryRepository : CurrentInventoryRepository) : ViewModel() {
    val currentInput = userRepository.currentInput.asLiveData()
    val heat: MutableLiveData<String> = MutableLiveData("")
    val blList = MediatorLiveData<List<String>?>()
    val quantityList = MediatorLiveData<List<String>?>()

    private fun setTime(): String{
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM_dd_yyyy HH:mm:ss")
        val timeNow: LocalDateTime = LocalDateTime.now()
        return formatter.format(timeNow)
    }

    private val triggerLoader = MutableLiveData<Unit>()

    fun refresh() {
        triggerLoader.value = Unit
    }

    val isSearchVis = mutableStateOf(false)

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

    fun findByBaseHeat(heat: String): LiveData<List<CurrentInventoryLineItem>?> {
        val result = MutableLiveData<List<CurrentInventoryLineItem>?>()
        viewModelScope.launch {
            val returnedResult = inventoryRepository.findByBaseHeat(heat)
            result.postValue(returnedResult)
        }
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

    fun addNewItemByBaseHeat() {
        val lineItem = newItemByBaseHeat()
        val mediatorLiveData = MediatorLiveData<Boolean>()
        mediatorLiveData.addSource(lineItem) {
            mediatorLiveData.removeSource(lineItem)
            mediatorLiveData.value = it.blNum != "N/A"

            ScannedInfo.getValues(it)
            insert(it)
        }
    }

    private fun newItemByBaseHeat(): LiveData<CurrentInventoryLineItem>{
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

    fun getReturnType() : LiveData<String> {
        val mediatorLiveData = MediatorLiveData<String>()
        mediatorLiveData.addSource(getBlList()) { blList ->

            when {
                blList.isNullOrEmpty() -> mediatorLiveData.value = "Null"
                blList.isNotEmpty() -> {
                    mediatorLiveData.removeSource(getBlList())

                    mediatorLiveData.addSource(getQuantityList()) { quantityList ->

                        when {
                            quantityList.isNullOrEmpty() -> mediatorLiveData.value = "Null"
                            quantityList.isNotEmpty() -> {
                                mediatorLiveData.removeSource(getQuantityList())
                                when {
                                    blList.size == 1 && quantityList.size == 1 -> {
                                        mediatorLiveData.value = "Single Return"
                                        addNewItemByBaseHeat()
                                        ScannedInfo.setValues(heat.value!!, blNum = blList[0])
                                        Log.d("DEBUG", "THe VALUE IS ${blList[0]}")
                                    }
                                    blList.size > 1 && quantityList.size == 1 -> mediatorLiveData.value = "Multiple Bls"

                                    blList.size == 1 && quantityList.size > 1 -> mediatorLiveData.value = "Multiple Quantities"
                                    else -> mediatorLiveData.value = "Multiple Bls and Quantities"
                                }
                            }
                        }
                    }
                }
            }
        }
        return mediatorLiveData
    }

    private fun getQuantityList(): LiveData<List<String>?> {

        if (isSearchVis.value) {
            val repositoryLiveData = findByBaseHeat("%${heat.value}%")

            quantityList.addSource(repositoryLiveData) { items: List<CurrentInventoryLineItem>? ->
                if (repositoryLiveData.value.isNullOrEmpty() || repositoryLiveData.value!!.isEmpty()) {
                    quantityList.value = null
                } else {
                    quantityList.removeSource(repositoryLiveData)

                    items?.let {
                        val quantityList = mutableListOf<String>()

                        for (item in items) {
                            if (quantityList.find { it == item.quantity!! } == null) {
                                Log.d("DEBUG", "quantity")
                                quantityList.add(item.quantity!!)
                            }
                        }
                       this.quantityList.value = quantityList.toList()
                    }
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

    private fun getBlList(): LiveData<List<String>?> {
        if (isSearchVis.value) {
            val repositoryLiveData = findByBaseHeat("%${heat.value}%")

            blList.addSource(repositoryLiveData) { items: List<CurrentInventoryLineItem>? ->
                if (repositoryLiveData.value.isNullOrEmpty()) {
                    blList.value = null
                } else {
                    blList.removeSource(repositoryLiveData)

                    items?.let {
                        val blList = mutableListOf<String>()


                        for (item in items) {
                            if (blList.find { it == item.blNum } == null) {
                                Log.d("DEBUG", "bl")
                                blList.add(item.blNum!!)
                            }
                        }
                        this.blList.value = blList.toList()
                    }
                }
            }
        }
        return blList
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