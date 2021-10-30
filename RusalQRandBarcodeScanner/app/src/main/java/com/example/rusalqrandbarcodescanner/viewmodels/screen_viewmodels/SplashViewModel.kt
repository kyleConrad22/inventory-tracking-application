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
    val uiState = mutableStateOf<SplashState>(SplashState.Base)

    init {
        mainActivityVM.loading.value = true
        viewModelScope.launch {
            if (repo.getAllSuspend().isNullOrEmpty()) {
                /* TODO - add logic to notify user when local database is empty and connection to server cannot be established
                *   Again notify user when connection to server was successfully established and sync is starting */
            }
            val items = repo.getAddedItems()
            if (items.isNotEmpty()) {
                uiState.value = SplashState.Recreation
                mainActivityVM.recreateSession(items[0])
                mainActivityVM.refresh()
                destination.value = Screen.InfoInputScreen.title
                mainActivityVM.loading.value = false
            } else {
                mainActivityVM.showSnackBar("Syncing with database, this may take some time...")
                mainActivityVM.updateLocalDatabase()
                destination.value = Screen.MainMenuScreen.title
            }
        }
    }

    private fun updateLocalDatabase() {

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

sealed class SplashState {
    object Recreation : SplashState()
    object Base : SplashState()
}