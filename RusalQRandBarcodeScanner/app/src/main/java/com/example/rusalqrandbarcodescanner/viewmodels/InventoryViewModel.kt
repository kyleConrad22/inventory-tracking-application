package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.database.RusalItem
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class InventoryViewModel(private val repository: InventoryRepository): ViewModel() {

    fun findByBarcode(barcode: String): LiveData<RusalItem?> {
        val result = MutableLiveData<RusalItem?>()
        viewModelScope.launch {
            val returnedResult = repository.findByBarcode(barcode)
            result.postValue(returnedResult)
        }
        return result
    }

    fun insert(lineItem: RusalItem) = viewModelScope.launch {
        repository.insert(lineItem)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    class CurrentInventoryViewModelFactory(private val repository: InventoryRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InventoryViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}