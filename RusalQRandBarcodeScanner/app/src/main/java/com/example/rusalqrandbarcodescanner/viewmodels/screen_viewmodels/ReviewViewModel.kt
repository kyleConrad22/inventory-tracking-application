package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import kotlin.properties.Delegates

class ReviewViewModel(private val invRepo : InventoryRepository) : ViewModel() {
    private var loadedBundles by Delegates.notNull<Int>()

    val loading = mutableStateOf(true)
    val items : MutableState<List<RusalItem>> = mutableStateOf(listOf())


    init {
        viewModelScope.launch {
            loadedBundles = invRepo.getNumberOfAddedItems()
            items.value = invRepo.getAddedItems()
            loading.value = false
        }
    }

    fun removeItem(item : RusalItem) = viewModelScope.launch {
        if (item.barcode.contains("u")) {
            invRepo.delete(item)
        } else {
            invRepo.updateIsAddedStatus(false, item.heatNum)
        }
        items.value = invRepo.getAddedItems()
        loadedBundles = invRepo.getNumberOfAddedItems()
    }

    fun removeAllAddedItems() = viewModelScope.launch {
        invRepo.removeAllAddedItems()
    }

    class ReviewViewModelFactory(private val invRepo : InventoryRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewViewModel(invRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}