package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class ReviewViewModel(private val invRepo : InventoryRepository, private val mainActivityVM: MainActivityViewModel) : ViewModel() {

    fun removeItem(item : RusalItem) = viewModelScope.launch {
        if ('u' in item.barcode || 'n' in item.barcode) {
            invRepo.delete(item)
        } else {
            if (mainActivityVM.sessionType.value == SessionType.SHIPMENT) invRepo.removeItemFromShipment(item.heatNum)
            else invRepo.removeItemFromReception(item.heatNum)
        }
        mainActivityVM.refresh()
    }

    // Clears added items from current session - does not delete from repo or reset shipment / reception specific fields
    fun clearAddedItems() = viewModelScope.launch {
        invRepo.removeAllAddedItems()
        mainActivityVM.refresh()
    }

    fun initiateUpdate() {
        HttpRequestHandler.initUpdate(mainActivityVM.addedItems.value, mainActivityVM.sessionType.value, mainActivityVM.getApplication())
        mainActivityVM.showSnackBar("Starting sync with database...")
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

    companion object {
        private const val TAG = "ReviewViewModel"
    }
}