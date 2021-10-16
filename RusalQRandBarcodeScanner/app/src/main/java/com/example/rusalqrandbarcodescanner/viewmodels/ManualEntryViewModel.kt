package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

@DelicateCoroutinesApi
class ManualEntryViewModel(private val userRepository : UserInputRepository) : ViewModel() {

    val heat = mutableStateOf("")
    val loading = mutableStateOf(false)
    val isSearchVis = mutableStateOf(false)

    // Used to manually test logic for setSearchVis as heat number is typed
    fun refresh() {
        setSearchVis()
    }

    // Saves heat input to repository
    fun updateHeat() {
        loading.value = true
        GlobalScope.launch(Dispatchers.Main) {
            val value = GlobalScope.async {
                withContext(Dispatchers.Main) {
                    heat.value.let { userRepository.updateHeat(it.replace("-","")) }
                }
            }
            value.await()
            loading.value = false
        }
    }

    // Sets the visibility of search button
    private fun setSearchVis() {
        isSearchVis.value = heat.value.length > 5
    }

    class ManualEntryViewModelFactory(private val userRepository : UserInputRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(ManualEntryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ManualEntryViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}