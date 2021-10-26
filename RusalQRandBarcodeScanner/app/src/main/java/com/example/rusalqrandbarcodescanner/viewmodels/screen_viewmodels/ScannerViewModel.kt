package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rusalqrandbarcodescanner.util.inputvalidation.QRValidator
import java.lang.IllegalArgumentException

class ScannerViewModel() : ViewModel() {
    val rawValue = mutableStateOf("")
    val uiState = mutableStateOf<ScannerState>(ScannerState.Scanning)

    fun checkIsValid() {
        uiState.value = ScannerState.Loading
        uiState.value =
            if (QRValidator.isValidRusalCode()) ScannerState.ValidScan
            else ScannerState.InvalidScan
    }



    class ScannerViewModelFactory() : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(ScannerViewModel::class.java)) {
                return ScannerViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }
}

sealed class ScannerState {
    object Scanning : ScannerState()
    object ValidScan : ScannerState()
    object InvalidScan : ScannerState()
    object Loading : ScannerState()
}