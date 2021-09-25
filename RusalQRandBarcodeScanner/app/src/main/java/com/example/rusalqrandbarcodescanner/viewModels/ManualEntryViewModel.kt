package com.example.rusalqrandbarcodescanner.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.util.ScannedInfo
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DelicateCoroutinesApi
class ManualEntryViewModel(private val userRepository : UserInputRepository) : ViewModel() {
    private val triggerLoader = MutableLiveData<Unit>()

    val heat: MutableLiveData<String> = MutableLiveData("")
    val loading = mutableStateOf(false)
    val isSearchVis : LiveData<Boolean> = triggerLoader.switchMap { setSearchVis() }

    // Used to manually test logic for setSearchVis as heat number is typed
    fun refresh() {
        triggerLoader.value = Unit
    }

    // Saves heat input to repository
    fun updateHeat() {
        loading.value = true
        GlobalScope.launch(Dispatchers.Main) {
            val value = GlobalScope.async {
                withContext(Dispatchers.Main) {
                    heat.value?.let { userRepository.updateHeat(it) }
                }
            }
            println(value.await())
            loading.value = false
        }
    }

    // Sets the visibility of search button
    private fun setSearchVis() : LiveData<Boolean> {
        val mediatorLiveData : MediatorLiveData<Boolean> = MediatorLiveData()
        mediatorLiveData.addSource(heat) {
            mediatorLiveData.removeSource(heat)
            mediatorLiveData.value = it.length > 5
        }

        return mediatorLiveData
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