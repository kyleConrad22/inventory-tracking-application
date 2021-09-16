package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import java.lang.IllegalArgumentException

class BundleInfoViewModel(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository) : ViewModel() {
    class BundleInfoViewModelFactory(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(BundleAddedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BundleInfoViewModel(userRepo, codeRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}