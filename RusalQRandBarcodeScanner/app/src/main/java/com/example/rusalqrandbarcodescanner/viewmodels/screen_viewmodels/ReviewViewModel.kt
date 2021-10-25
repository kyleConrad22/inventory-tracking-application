package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.services.FileStorage
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import com.example.rusalqrandbarcodescanner.services.worker.ReceptionUploadWorker
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import java.util.*

class ReviewViewModel(private val invRepo : InventoryRepository, private val mainActivityVM: MainActivityViewModel) : ViewModel() {

    fun removeItem(item : RusalItem) = viewModelScope.launch {
        if (item.barcode.contains("u")) {
            invRepo.delete(item)
        } else {
            if (mainActivityVM.sessionType.value == SessionType.SHIPMENT) invRepo.removeItemFromShipment(item.heatNum)
            else invRepo.removeItemFromReception(item.heatNum)
        }
        mainActivityVM.refresh()
    }

    fun removeAllAddedItems() = viewModelScope.launch {
        mainActivityVM.addedItems.value.forEach { it ->
            if (it.barcode.contains('u')) {
                removeItem(it)
            }
        }
        invRepo.removeAllAddedItems()
        mainActivityVM.refresh()
    }

    fun initiateUpdate() {
        HttpRequestHandler.initUpdate(mainActivityVM.addedItems.value, mainActivityVM.sessionType.value, mainActivityVM.getApplication())
    }

    class ReviewViewModelFactory(private val invRepo : InventoryRepository, private val mainActivityVM: MainActivityViewModel) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewViewModel(invRepo, mainActivityVM) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}