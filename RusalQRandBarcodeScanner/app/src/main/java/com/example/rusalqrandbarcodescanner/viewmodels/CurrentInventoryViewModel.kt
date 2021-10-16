package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.database.RusalItem
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class CurrentInventoryViewModel(private val repository: CurrentInventoryRepository): ViewModel() {

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