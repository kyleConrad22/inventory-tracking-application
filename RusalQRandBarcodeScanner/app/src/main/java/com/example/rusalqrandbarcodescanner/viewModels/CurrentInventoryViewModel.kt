package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class CurrentInventoryViewModel(private val repository: CurrentInventoryRepository): ViewModel() {

    val allCodes: LiveData<List<CurrentInventoryLineItem>> = repository.fullInventory.asLiveData()

    fun insert(scannedCode: CurrentInventoryLineItem) = viewModelScope.launch {
        repository.insert(scannedCode)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun findByHeat(barcode: String): LiveData<CurrentInventoryLineItem?> {
        val result = MutableLiveData<CurrentInventoryLineItem?>()
        viewModelScope.launch {
            val returnedCode = repository.findByBarcode(barcode)
            result.postValue(returnedCode)
        }
        return result
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