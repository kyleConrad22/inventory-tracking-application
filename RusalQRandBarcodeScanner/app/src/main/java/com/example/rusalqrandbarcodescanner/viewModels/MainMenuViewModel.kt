package com.example.rusalqrandbarcodescanner.viewModels

import android.util.Log
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainMenuViewModel(private val repository : UserInputRepository) : ViewModel() {
    private val currentInput = repository.currentInput.asLiveData()

    fun isLoad(isLoad: Boolean) = viewModelScope.launch {
        val userInput: UserInput = if (isLoad) {
            UserInput(type = "Load", id = "data")
        } else {
            UserInput(type = "Reception", id = "data")
        }
        if (hasData()) {
            repository.update(userInput)
        } else {
            repository.insert(userInput)
        }
    }

    private fun hasData(): Boolean {
        return (currentInput.value != null) && currentInput.value!!.isNotEmpty() && (currentInput.value!![0].id == "data")
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