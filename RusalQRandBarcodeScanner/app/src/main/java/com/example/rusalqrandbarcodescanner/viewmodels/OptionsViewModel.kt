package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class OptionsViewModel(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository) : ViewModel() {

    val userInput : MutableState<UserInput?> = mutableStateOf(null)
    val loading = mutableStateOf(false)
    val isLoad = mutableStateOf(false)
    val isDisplayAdditionalButtons = mutableStateOf(false)
    val isDisplayRemoveEntry = mutableStateOf(false)

    init {
        viewModelScope.launch{
            loading.value = true
            userInput.value = userRepo.getInputSuspend()!![0]
            isLoad.value = getIsLoad(userInput.value!!)

            setDisplayableButtons(
                codeRepo.getRowCount() ?: 0,
                if (userInput.value!!.bundleQuantity != "")
                    userInput.value!!.bundleQuantity!!.toInt()
                else
                    0
            )
            loading.value = false
        }

    }

    private fun setDisplayableButtons(count : Int, requestedQuantity : Int) {
        isDisplayAdditionalButtons.value = count > 0
        isDisplayRemoveEntry.value = requestedQuantity != 0 && requestedQuantity - count > 0

    }

    fun deleteAll() = viewModelScope.launch {
        codeRepo.deleteAll()
    }

    private fun getIsLoad(userInput : UserInput) : Boolean {
        return userInput.type == "Load"
    }

    class OptionsViewModelFactory(private val userRepository : UserInputRepository, private val codeRepository : CodeRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(OptionsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return OptionsViewModel(userRepository, codeRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}