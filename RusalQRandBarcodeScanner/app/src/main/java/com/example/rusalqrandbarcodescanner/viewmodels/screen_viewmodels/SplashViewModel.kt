package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class SplashViewModel(private val repo : InventoryRepository, private val mainActivityVM : MainActivityViewModel) : ViewModel() {
    val destination = mutableStateOf("")

    init {
        mainActivityVM.loading.value = true
        viewModelScope.launch {
            val items = repo.getAddedItems()
            if (items.isNotEmpty()) {
                mainActivityVM.recreateSession(items[0])
                mainActivityVM.refresh()
                destination.value = Screen.InfoInputScreen.title
                mainActivityVM.loading.value = false
            } else {
                mainActivityVM.updateLocalDatabase()
                destination.value = Screen.MainMenuScreen.title
            }
        }
    }

    class SplashViewModelFactory(private val repo : InventoryRepository, private val mainActivityVM : MainActivityViewModel) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(SplashViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SplashViewModel(repo, mainActivityVM) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}