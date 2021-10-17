package com.example.rusalqrandbarcodescanner.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainActivityViewModel(private val repo : InventoryRepository, application : Application): AndroidViewModel(application) {

    val loading = mutableStateOf(true)
    val sessionType = mutableStateOf(SessionType.GENERAL)

    val checker = mutableStateOf("")
    val loader = mutableStateOf("")
    val loadNum = mutableStateOf("")
    val workOrder = mutableStateOf("")
    val barge = mutableStateOf("")
    val bl = mutableStateOf("")
    val pieceCount = mutableStateOf("")
    val quantity = mutableStateOf("")
    val heatNum = mutableStateOf("")

    val addedItemCount = mutableStateOf(0)
    val addedItems = mutableStateOf(listOf<RusalItem>())

    val displayRemoveEntryContent = mutableStateOf(false)

    init {
        viewModelScope.launch {
            loading.value = true
            repo.deleteAll()
            HttpRequestHandler.initialize(repo, loading)
        }
    }

    fun refresh() {
        updateAddedItems()
        updateAddedItemCount()
    }

    /* TODO */
    fun deleteBaseHeat() {
        if (heatNum.value.length == 6) {
        }
    }

    private fun setDisplayRemoveEntryContent() {
        displayRemoveEntryContent.value = addedItemCount.value - quantity.value.toInt() > 0
    }

    private fun updateAddedItemCount() = viewModelScope.launch() {
        addedItemCount.value = repo.getNumberOfAddedItems()
        setDisplayRemoveEntryContent()
    }

    fun removeAllAddedItems() = viewModelScope.launch {
        repo.removeAllAddedItems()
        updateAddedItemCount()
    }

    private fun updateAddedItems() = viewModelScope.launch {
       addedItems.value = repo.getAddedItems()
    }

    fun findByBarcode(barcode: String): LiveData<RusalItem?> {
        val result = MutableLiveData<RusalItem?>()
        viewModelScope.launch {
            val returnedResult = repo.findByBarcode(barcode)
            result.postValue(returnedResult)
        }
        return result
    }

    fun insert(lineItem: RusalItem) = viewModelScope.launch {
        repo.insert(lineItem)
    }

    fun deleteAll() = viewModelScope.launch {
        repo.deleteAll()
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