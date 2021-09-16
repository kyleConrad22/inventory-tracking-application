package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import java.lang.IllegalArgumentException

class ScannerViewModel(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository, private val invRepo : CurrentInventoryRepository) : ViewModel() {
    class ScannerViewModelFactory(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository, private val invRepo : CurrentInventoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ScannerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ScannerViewModel(userRepo, codeRepo, invRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}