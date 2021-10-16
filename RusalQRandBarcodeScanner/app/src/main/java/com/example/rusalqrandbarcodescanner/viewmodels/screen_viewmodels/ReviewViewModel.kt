package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import kotlin.properties.Delegates

class ReviewViewModel(private val invRepo : InventoryRepository, private val userRepo : UserInputRepository) : ViewModel() {
    private lateinit var currentInput : MutableState<UserInput>
    private var loadedBundles by Delegates.notNull<Int>()

    val loading = mutableStateOf(true)
    val codes : MutableState<List<RusalItem>> = mutableStateOf(listOf())


    init {
        viewModelScope.launch {
            currentInput = mutableStateOf(userRepo.getInputSuspend()!![0])
            loadedBundles = invRepo.getNumberOfAddedItems()
            codes.value = invRepo.getAddedItems()
            loading.value = false
        }
    }

    fun removeCode(item : RusalItem) = viewModelScope.launch {
        if (item.barcode.contains("u")) {
            invRepo.delete(item)
        } else {
            invRepo.updateIsAddedStatus(false, item.heatNum)
        }
        codes.value = invRepo.getAddedItems()
        loadedBundles = invRepo.getNumberOfAddedItems()
    }

    fun removeAllAddedItems() = viewModelScope.launch {
        invRepo.removeAllAddedItems()
    }

    fun isLoad() : Boolean {
        return currentInput.value.type == "Load"
    }

    fun showRemoveDialog() : Boolean {
        return Integer.parseInt(currentInput.value.bundleQuantity) - loadedBundles > 0
    }

    class ReviewViewModelFactory(private val invRepo : InventoryRepository, private val userRepo: UserInputRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewViewModel(invRepo, userRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}