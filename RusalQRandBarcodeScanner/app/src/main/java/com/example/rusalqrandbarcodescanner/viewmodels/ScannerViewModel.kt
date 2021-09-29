package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import com.example.rusalqrandbarcodescanner.util.ScannedInfo
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

@DelicateCoroutinesApi
class ScannerViewModel(private val userRepo : UserInputRepository) : ViewModel() {

    val loading = mutableStateOf(false)
    val heat = mutableStateOf("")

    /* TODO - Move Scanning Logic To ViewModel */

    fun updateHeat() {
        loading.value = true
        GlobalScope.launch(Dispatchers.Main) {
            val value = GlobalScope.async {
                withContext(Dispatchers.Main) {
                    userRepo.updateHeat(ScannedInfo.heatNum)
                }
            }
            value.await()
            loading.value = false
        }
    }

    class ScannerViewModelFactory(private val userRepo : UserInputRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ScannerViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ScannerViewModel(userRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}