package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class CurrentInventoryViewModel(private val repository: CurrentInventoryRepository): ViewModel() {

    val allCodes: LiveData<List<CurrentInventoryLineItem>> = repository.fullInventory.asLiveData()

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