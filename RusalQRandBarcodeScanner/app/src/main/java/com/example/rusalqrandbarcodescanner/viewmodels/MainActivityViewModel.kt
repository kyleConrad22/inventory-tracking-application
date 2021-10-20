package com.example.rusalqrandbarcodescanner.viewmodels

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import com.example.rusalqrandbarcodescanner.util.Commodity
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
    val inboundItemCount = mutableStateOf(0)
    val addedItems = mutableStateOf(listOf<RusalItem>())
    val receivedItemCount = mutableStateOf(0)

    val displayRemoveEntryContent = mutableStateOf(false)
    val isSessionStarted = mutableStateOf(false)

    init {
        viewModelScope.launch {
            loading.value = true
            if (repo.getAddedItems().isNotEmpty()) {
                /*TODO - Add Code for if session already started on startup to recreate session*/
                repo.deleteAll()
                HttpRequestHandler.initialize(repo, loading)
            } else {
                repo.deleteAll()
                HttpRequestHandler.initialize(repo, loading)
            }
        }
    }

    fun refresh() {
        updateAddedItems()
        updateAddedItemCount()
        updateInboundItemCount()
        updateReceivedItemCount()
    }

    fun clearInputFields() {
        workOrder.value = ""
        checker.value = ""
        loadNum.value = ""
        loader.value = ""
        barge.value = ""
        heatNum.value = ""
        quantity.value = ""
        pieceCount.value = ""
        bl.value = ""
    }

    private fun updateReceivedItemCount() = viewModelScope.launch {
        receivedItemCount.value = repo.getReceivedItemCount(barge.value)
    }

    private fun updateInboundItemCount() = viewModelScope.launch {
        inboundItemCount.value = repo.getInboundItemCount(barge.value)
    }

    private fun setDisplayRemoveEntryContent() {
        if (sessionType.value == SessionType.SHIPMENT) {
            val calcQuantity : Int = if (quantity.value == "") { 0 } else { quantity.value.toInt() }

            displayRemoveEntryContent.value = calcQuantity - addedItemCount.value > 0

        } else {
            displayRemoveEntryContent.value = false
        }
    }

    private fun updateAddedItemCount() = viewModelScope.launch() {
        addedItemCount.value = repo.getNumberOfAddedItems()
        setDisplayRemoveEntryContent()
    }

    fun getItemCommodity(item : RusalItem) : Commodity {
        if (item.grade.contains("INGOTS")) {
            return Commodity.INGOTS
        }
        return Commodity.BILLETS
    }

    fun removeAllAddedItems() = viewModelScope.launch {
        repo.removeAllAddedItems()
        updateAddedItemCount()
    }

    private fun updateAddedItems() = viewModelScope.launch {
       addedItems.value = repo.getAddedItems()
    }

    fun insert(lineItem: RusalItem) = viewModelScope.launch {
        repo.insert(lineItem)
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