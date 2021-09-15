package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import java.lang.IllegalArgumentException

class QuantityOptionsViewModel(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository, private val invRepo : CurrentInventoryViewModel) : ViewModel() {
    class QuantityOptionsViewModelFactory(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository, private val invRepo : CurrentInventoryViewModel) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(QuantityOptionsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return QuantityOptionsViewModel(userRepo, codeRepo, invRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}