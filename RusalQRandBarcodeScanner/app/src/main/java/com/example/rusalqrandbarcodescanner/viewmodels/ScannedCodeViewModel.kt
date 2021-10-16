package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ScannedCodeViewModel(private val repository: CodeRepository): ViewModel() {

    val allCodes: LiveData<List<ScannedCode>> = repository.allCodes.asLiveData()

    fun insert(scannedCode: ScannedCode) = viewModelScope.launch {
        repository.insert(scannedCode)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun delete(scannedCode: ScannedCode) = viewModelScope.launch{
        repository.delete(scannedCode)
    }

    class ScannedCodeViewModelFactory(private val repository: CodeRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ScannedCodeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ScannedCodeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}