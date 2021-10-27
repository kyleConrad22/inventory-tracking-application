package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.util.QRParser
import com.example.rusalqrandbarcodescanner.util.inputvalidation.QRValidator
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import java.lang.IllegalArgumentException

class ScannerViewModel(private val mainActivityVM : MainActivityViewModel) : ViewModel() {
    val uiState = mutableStateOf<ScannerState>(ScannerState.Scanning)

    // Checks if scanned code is valid and sets UI State accordingly
    private fun checkIsValid(rawValue : String) {
        uiState.value = ScannerState.Loading
        uiState.value =
            if (QRValidator.isValidRusalCode(rawValue)) ScannerState.ValidScan
            else ScannerState.InvalidScan
    }

    // Logic to be taken on scan, if valid code sends relevant retrieved information to Main Activity ViewModel
    fun onScan(rawValue : String, onValidScan : () -> Unit) {
        checkIsValid(rawValue)
        if (uiState.value == ScannerState.ValidScan) {
            val scannedItem : RusalItem = QRParser.parseRusalCode(rawValue)
            Log.d(TAG, scannedItem.heatNum)
            mainActivityVM.heatNum.value = scannedItem.heatNum
            if (mainActivityVM.sessionType.value == SessionType.RECEPTION) {
                mainActivityVM.scannedItem = scannedItem
            }
            onValidScan()
        }
    }

    class ScannerViewModelFactory(private val mainActivityVM : MainActivityViewModel) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(ScannerViewModel::class.java)) {
                return ScannerViewModel(mainActivityVM) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }

    }

    companion object {
        private const val TAG = "ScannerViewModel"
    }
}

sealed class ScannerState {
    object Scanning : ScannerState()
    object ValidScan : ScannerState()
    object InvalidScan : ScannerState()
    object Loading : ScannerState()
}