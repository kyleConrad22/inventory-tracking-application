package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class ReviewViewModel(private val codeRepository : CodeRepository, private val inventoryRepository: CurrentInventoryRepository, private val userRepository: UserInputRepository) : ViewModel() {
    private val currentInput = userRepository.currentInput.asLiveData()
    private val count = codeRepository.count.asLiveData()
    val codes = codeRepository.allCodes.asLiveData()

    init {
        
    }

    val isLoad = mutableStateOf(currentInput.value!![0].type == "Load")
    val loadType = mutableStateOf(currentInput.value!![0].type)

    fun deleteAll() = viewModelScope.launch {
        codeRepository.deleteAll()
    }

    val showRemoveDialog = mutableStateOf(count.value != null && currentInput.value != null && Integer.parseInt(currentInput.value!![0].bundleQuantity!!) - count.value!! > 0)

    class ReviewViewModelFactory(private val codeRepository: CodeRepository, private val inventoryRepository: CurrentInventoryRepository, private val userRepository: UserInputRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewViewModel(codeRepository, inventoryRepository, userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}