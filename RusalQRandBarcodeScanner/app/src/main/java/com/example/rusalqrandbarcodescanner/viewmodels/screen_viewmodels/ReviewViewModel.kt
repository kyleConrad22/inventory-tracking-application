package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import kotlin.properties.Delegates

class ReviewViewModel(private val codeRepo : CodeRepository, private val userRepo : UserInputRepository) : ViewModel() {
    private lateinit var currentInput : MutableState<UserInput>
    private var loadedBundles by Delegates.notNull<Int>()

    val loading = mutableStateOf(true)
    val codes : MutableState<List<ScannedCode>> = mutableStateOf(listOf())


    init {
        viewModelScope.launch {
            currentInput = mutableStateOf(userRepo.getInputSuspend()!![0])
            loadedBundles = codeRepo.getRowCount()!!
            codes.value = codeRepo.getAllCodes()
            loading.value = false
        }
    }

    fun removeCode(code : ScannedCode) = viewModelScope.launch {
        codeRepo.delete(code)
        codes.value = codeRepo.getAllCodes()
        loadedBundles = codeRepo.getRowCount()!!
    }

    fun deleteAll() = viewModelScope.launch {
        codeRepo.deleteAll()
    }

    fun isLoad() : Boolean {
        return currentInput.value.type == "Load"
    }

    fun showRemoveDialog() : Boolean {
        return Integer.parseInt(currentInput.value.bundleQuantity) - loadedBundles > 0
    }

    class ReviewViewModelFactory(private val codeRepository: CodeRepository, private val userRepository: UserInputRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReviewViewModel(codeRepository, userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}