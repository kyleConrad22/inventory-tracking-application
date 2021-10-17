package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class ReviewViewModel(private val invRepo : InventoryRepository, private val mainActivityVM: MainActivityViewModel) : ViewModel() {

    fun removeItem(item : RusalItem) = viewModelScope.launch {
        if (item.barcode.contains("u")) {
            invRepo.delete(item)
        } else {
            invRepo.updateIsAddedStatus(false, item.heatNum)
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