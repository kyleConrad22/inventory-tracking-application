package com.example.rusalqrandbarcodescanner.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.ScannedInfo
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CurrentInventoryViewModel(private val repository: CurrentInventoryRepository): ViewModel() {

    private val blListMediator = MediatorLiveData<List<String>?>()
    private val quantListMediator = MediatorLiveData<List<String>?>()

    private fun setTime(): String{
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM_dd_yyyy HH:mm:ss")
        val timeNow: LocalDateTime = LocalDateTime.now()
        return formatter.format(timeNow)
    }

    fun findByBaseHeat(heat: String): LiveData<List<CurrentInventoryLineItem>?> {
        val result = MutableLiveData<List<CurrentInventoryLineItem>?>()
        viewModelScope.launch {
            val returnedResult = repository.findByBaseHeat(heat)
            result.postValue(returnedResult)
        }
        return result
    }

    fun findByBarcode(barcode: String): LiveData<CurrentInventoryLineItem?> {
        val result = MutableLiveData<CurrentInventoryLineItem?>()
        viewModelScope.launch {
            val returnedResult = repository.findByBarcode(barcode)
            result.postValue(returnedResult)
        }
        return result
    }

    fun findByBarcodes(barcode: String) : LiveData<List<CurrentInventoryLineItem>?> {
        val result = MutableLiveData<List<CurrentInventoryLineItem>?>()
        viewModelScope.launch {
            val codes = repository.findByBarcodes(barcode)
            result.postValue(codes)
        }
        return result
    }

    fun insert(lineItem: CurrentInventoryLineItem) = viewModelScope.launch {
        repository.insert(lineItem)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun findByHeat(heat: String): LiveData<CurrentInventoryLineItem?> {
        val result = MutableLiveData<CurrentInventoryLineItem?>()
        viewModelScope.launch {
            val returnedCode = repository.findByHeat(heat)
            result.postValue(returnedCode)
        }
        return result
    }

    fun addNewItemByBaseHeat(heat: String, userInputViewModel: UserInputViewModel): LiveData<Boolean>{
        val lineItem = newItemByBaseHeat(heat, userInputViewModel)
        val mediatorLiveData = MediatorLiveData<Boolean>()
        mediatorLiveData.addSource(lineItem) {
            mediatorLiveData.removeSource(lineItem)
            mediatorLiveData.value = it.blNum != "N/A"

            ScannedInfo.getValues(it)
            insert(it)
        }
        return mediatorLiveData
    }

    fun newItemByBaseHeat(heat: String, userInputViewModel: UserInputViewModel): LiveData<CurrentInventoryLineItem>{
        val listByHeat = findByBaseHeat("%$heat%")
        val mediatorLiveData = MediatorLiveData<CurrentInventoryLineItem>()
        mediatorLiveData.addSource(listByHeat) {byHeat ->
            mediatorLiveData.removeSource(listByHeat)

            Log.d("DEBUG", "made it here")
            if (byHeat != null) {
                val listByBarcode = findByBarcodes("%${heat}u%")
                mediatorLiveData.addSource(listByBarcode) { byBarcode ->
                    mediatorLiveData.removeSource(listByBarcode)

                    val blList = blListMediator.value
                    val quantList = quantListMediator.value

                    val barcode = if (byBarcode == null) { "${heat}u1" } else { "${heat}u${byBarcode.size + 1}" }
                    Log.d("DEBUG",  barcode)
                    val bl = blList!![0]
                    val quantity = quantList!![0]
                    val dimension = byHeat[0].dimension
                    val grade = byHeat[0].grade
                    val certificateNum = byHeat[0].certificateNum

                    mediatorLiveData.value = CurrentInventoryLineItem(
                        heatNum = heat,
                        packageNum = "N/A",
                        grossWeightKg = "N/A",
                        netWeightKg = "N/A",
                        quantity = quantity,
                        dimension = dimension,
                        grade = grade,
                        certificateNum = certificateNum,
                        blNum = bl,
                        barcode = barcode,
                        workOrder = userInputViewModel.order.value,
                        loadNum = userInputViewModel.load.value,
                        loader = userInputViewModel.loader.value,
                        loadTime = setTime()
                    )
                }
            } else {
                mediatorLiveData.value =
                    CurrentInventoryLineItem(
                        heatNum = heat,
                        packageNum = "N/A",
                        grossWeightKg = "N/A",
                        netWeightKg = "N/A",
                        quantity = "N/A",
                        dimension = "N/A",
                        grade = "N/A",
                        certificateNum = "N/A",
                        blNum = "N/A",
                        barcode = "N/A",
                        workOrder = userInputViewModel.order.value,
                        loadNum = userInputViewModel.load.value,
                        loader = userInputViewModel.loader.value,
                        loadTime = setTime()
                    )
            }
        }
        return mediatorLiveData
    }

    fun getQuantList(heat: String?): LiveData<List<String>?> {
        if (heat == null) {
            quantListMediator.value = null

        } else {
            val repositoryLiveData = findByBaseHeat("%$heat%")
            quantListMediator.addSource(repositoryLiveData) { items: List<CurrentInventoryLineItem>? ->
                quantListMediator.removeSource(repositoryLiveData)

                items?.let {
                    val quantList = mutableListOf<String>()

                    for (item in items) {
                        if (quantList.find { it == item.quantity!! } == null) {
                            quantList.add(item.quantity!!)
                        }
                    }
                    quantListMediator.setValue(quantList.toList())
                }
            }
        }
        return quantListMediator
    }

    fun getBlList(heat: String?): LiveData<List<String>?> {
        if (heat == null){
            blListMediator.value = null

        } else {
            val repositoryLiveData = findByBaseHeat("%$heat%")
            blListMediator.addSource(repositoryLiveData) { items: List<CurrentInventoryLineItem>? ->
                blListMediator.removeSource(repositoryLiveData)

                items?.let {
                    val blList = mutableListOf<String>()

                    for (item in items) {
                        if (blList.find { it == item.blNum } == null) {
                            blList.add(item.blNum!!)
                        }
                    }
                    blListMediator.setValue(blList.toList())
                }
            }
        }
        return blListMediator
    }

    class CurrentInventoryViewModelFactory(private val repository: CurrentInventoryRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CurrentInventoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return CurrentInventoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}