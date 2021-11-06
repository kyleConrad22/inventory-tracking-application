package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import java.lang.IllegalArgumentException

class ManualEntryViewModel(private val mainActivityVM : MainActivityViewModel) : ViewModel() {

    val displaySearchButton = mutableStateOf(false)

    // Used to manually test logic for setSearchVis as heat number is typed
    internal fun refresh() {
        setSearchButtonVis()
    }

    // Sets the visibility of search button
    internal fun setSearchButtonVis() {
        displaySearchButton.value = mainActivityVM.heatNum.value.length > 5
    }

    class ManualEntryViewModelFactory(private val mainActivityVM : MainActivityViewModel) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(ManualEntryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ManualEntryViewModel(mainActivityVM) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}