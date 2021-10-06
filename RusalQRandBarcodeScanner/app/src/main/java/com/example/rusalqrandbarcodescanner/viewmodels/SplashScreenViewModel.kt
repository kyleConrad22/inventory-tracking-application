package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class SplashScreenViewModel(private val codeRepo : CodeRepository, private val invRepo : CurrentInventoryRepository) : ViewModel() {

    val loading = mutableStateOf(true)

    init {

        viewModelScope.launch {
            codeRepo.deleteAll()
            loading.value = HttpRequestHandler().initialize(invRepo)
        }
    }

    class SplashScreenViewModelFactory(private val codeRepo : CodeRepository, private val invRepo: CurrentInventoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(SplashScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SplashScreenViewModel(codeRepo, invRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}