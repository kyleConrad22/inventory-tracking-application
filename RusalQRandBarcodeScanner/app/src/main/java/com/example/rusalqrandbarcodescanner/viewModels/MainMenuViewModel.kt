package com.example.rusalqrandbarcodescanner.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class MainMenuViewModel(private val repository : UserInputRepository) : ViewModel() {
    private val currentInput = repository.currentInput.asLiveData()

    val loading = mutableStateOf(false)

    fun getIsLoad(isLoad : Boolean) {
        loading.value = true
        GlobalScope.launch(Dispatchers.Main) {
            isLoad(isLoad)
        }
    }
    suspend fun isLoad(isLoad: Boolean) {
        val value = GlobalScope.async { // Create worker thread
            withContext(Dispatchers.Main) {
                Log.d("DEBUG", "userInput")
                val userInput: UserInput =
                if (isLoad) {
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
        }
        println(value.await()) // Wait for worker thread to finish
        loading.value = false
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