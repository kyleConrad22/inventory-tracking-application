package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class SplashScreenViewModel(private val codeRepo : CodeRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            codeRepo.deleteAll()
        }
    }

    class SplashScreenViewModelFactory(private val codeRepo : CodeRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(SplashScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SplashScreenViewModel(codeRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}