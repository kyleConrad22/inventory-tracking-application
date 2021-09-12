package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainMenuViewModel(private val repository : UserInputRepository) : ViewModel() {
    fun isLoad(isLoad: Boolean) = viewModelScope.launch {
        val userInput: UserInput = if (isLoad) {
            UserInput(type = "Load")
        } else {
            UserInput(type = "Reception")
        }
        repository.insert(userInput)
    }

    class MainMenuViewModelFactory(private val repository: UserInputRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(MainMenuViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainMenuViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}