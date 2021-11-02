package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.services.ConnectivityHandler
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import java.util.*

class SplashViewModel(private val repo : InventoryRepository, private val mainActivityVM : MainActivityViewModel) : ViewModel() {

    val destination = mutableStateOf("")
    var uiState by mutableStateOf<SplashState>(SplashState.Base)
    var loading by mutableStateOf(true)

    private lateinit var liveData : LiveData<WorkInfo?>

    private val observer = Observer<WorkInfo?> { workInfo ->
        if (uiState !in listOf(SplashState.NoConnectionNonEmpty, SplashState.NoConnectionEmpty)) {
            loading = if (workInfo != null) {
                val state = workInfo.state
                Log.d(TAG, workInfo.state.toString())
                !state.isFinished
            } else {
                Log.d(TAG, "WorkInfo is null")
                true
            }
            Log.d(TAG, loading.toString())
        }
    }

    init {
        viewModelScope.launch {

            if (!ConnectivityHandler.isConnectedOrConnecting()) {
                uiState =
                    if (repo.getAllSuspend().isNullOrEmpty()) SplashState.NoConnectionEmpty
                    else SplashState.NoConnectionNonEmpty
            }

            val workerId : UUID

            val items = repo.getAddedItems()
            if (items.isNotEmpty()) {
                /* TODO - Initiate background update with WorkManager, add code once update method has been updated to no longer delete local database*/
                uiState = SplashState.Recreation
                mainActivityVM.recreateSession(items[0])
                mainActivityVM.refresh()
                destination.value = Screen.InfoInputScreen.title
                mainActivityVM.loading.value = false

            } else {
                mainActivityVM.showSnackBar("Attempting to sync with database, this may take some time...")
                workerId = HttpRequestHandler.startLocalDatabaseSync(mainActivityVM.getApplication())
                liveData = WorkManager.getInstance(mainActivityVM.getApplication())
                    .getWorkInfoByIdLiveData(workerId)
                liveData.observeForever(observer)
                destination.value = Screen.MainMenuScreen.title
            }

            if (uiState == SplashState.NoConnectionNonEmpty) {
                Log.d(TAG, "Starting")
                delay(5000L)
                Log.d(TAG, "Stopping")
                loading = false
            }
        }
    }

    override fun onCleared() {
        Log.d(TAG, "CLEARING")
        liveData.removeObserver(observer)
        super.onCleared()
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
    companion object {
        private const val TAG = "SplashScreenViewModel"
    }
}

sealed class SplashState {
    object Recreation : SplashState()
    object NoConnectionEmpty : SplashState()
    object NoConnectionNonEmpty : SplashState()
    object Updating : SplashState()
    object Base : SplashState()
}