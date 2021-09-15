package com.example.rusalqrandbarcodescanner.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainMenuViewModel(private val repository : UserInputRepository) : ViewModel() {
    private val currentInput = repository.currentInput.asLiveData()

    val loading = mutableStateOf(true)

    suspend fun isLoad(isLoad: Boolean) = viewModelScope.launch {
        Log.d("DEBUG", "userInput")
        val userInput: UserInput = if (isLoad) {
            UserInput(type = "Load", id = "data")
        } else {
            UserInput(type = "Reception", id = "data")
        }
        if (hasData()) {
            repository.update(userInput)
        } else {
            Log.d("DEBUG", "insert")
            repository.insert(userInput)
        }
    }

    private fun hasData(): Boolean {
        return !currentInput.value.isNullOrEmpty() && (currentInput.value!![0].id == "data")
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