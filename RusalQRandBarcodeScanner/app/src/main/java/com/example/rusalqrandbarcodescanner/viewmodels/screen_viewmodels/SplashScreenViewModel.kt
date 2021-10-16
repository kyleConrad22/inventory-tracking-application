package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class SplashScreenViewModel(private val invRepo : InventoryRepository) : ViewModel() {

    val loading = mutableStateOf(true)

    init {
        viewModelScope.launch {
            invRepo.deleteAll()
            loading.value = HttpRequestHandler.initialize(invRepo)
        }
    }

    class SplashScreenViewModelFactory(private val invRepo: InventoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(SplashScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SplashScreenViewModel(invRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}