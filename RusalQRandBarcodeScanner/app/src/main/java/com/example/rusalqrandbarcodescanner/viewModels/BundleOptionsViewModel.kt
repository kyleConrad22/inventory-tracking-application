package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import java.lang.IllegalArgumentException

class BundleOptionsViewModel(private val codeRepo : CodeRepository, private val invRepo : CurrentInventoryRepository) : ViewModel() {

    class BundleOptionsViewModelFactory(private val codeRepo : CodeRepository, private val invRepo : CurrentInventoryRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(BundleOptionsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BundleOptionsViewModel(codeRepo, invRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}