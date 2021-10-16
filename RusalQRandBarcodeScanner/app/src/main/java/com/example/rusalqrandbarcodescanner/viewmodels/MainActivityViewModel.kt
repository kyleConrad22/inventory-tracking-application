package com.example.rusalqrandbarcodescanner.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.database.RusalItem
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainActivityViewModel(private val repository: InventoryRepository, application : Application): AndroidViewModel(application) {

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

    class MainActivityViewModelFactory(private val repository: InventoryRepository, private val application : Application) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainActivityViewModel(repository, application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}