package com.example.rusalqrandbarcodescanner.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class CurrentInventoryViewModel(private val repository: CurrentInventoryRepository): ViewModel() {

    val allCodes: LiveData<List<CurrentInventoryLineItem>> = repository.fullInventory.asLiveData()
    val blListMediator = MediatorLiveData<List<String>>()
    val quantListMediator = MediatorLiveData<List<String>>()

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

    fun getQuantList(heat: String): LiveData<List<String>> {
        val repositoryLiveData = findByBaseHeat("%$heat%")
        quantListMediator.addSource(repositoryLiveData) { items: List<CurrentInventoryLineItem>? ->
            quantListMediator.removeSource(repositoryLiveData)
            items?.let{
                val quantList = mutableListOf<String>()
                for (item in items) {
                    if (quantList.find { it == item.quantity!! } == null) {
                        quantList.add(item.quantity!!)
                    }
                }
                quantListMediator.setValue(quantList.toList())
            }
        }
        return quantListMediator
    }

    fun getBlList(heat: String): LiveData<List<String>> {
        val repositoryLiveData = findByBaseHeat("%$heat%")
        blListMediator.addSource(repositoryLiveData) { items: List<CurrentInventoryLineItem>? ->
            blListMediator.removeSource(repositoryLiveData)
            items?.let{
                val blList = mutableListOf<String>()
                for (item in items) {
                    if (blList.find { it == item.blNum } == null) {
                        blList.add(item.blNum!!)
                    }
                }
                blListMediator.setValue(blList.toList())
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