package com.example.rusalqrandbarcodescanner.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rusalqrandbarcodescanner.util.ScannedInfo
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class IncorrectBundleViewModel(private val userRepo : UserInputRepository) : ViewModel() {
    private val initVal : Boolean? = null
    private var userInput : UserInput? = null

    val isIncorrectBl = mutableStateOf(initVal)
    val incorrectValue = mutableStateOf("")
    val loading = mutableStateOf(true)
    fun setIncorrectType() {

        if (isIncorrectBl.value == null) {
            GlobalScope.launch(Dispatchers.Main) {
                setUserInput()
                isIncorrectBl.value = userInput!!.bl!! != ScannedInfo.blNum
                incorrectValue.value = if (isIncorrectBl.value!!) { userInput!!.bl!! } else { userInput!!.bundleQuantity!! }
                loading.value = false
            }
        }
    }

    private suspend fun setUserInput() {
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                userInput = userRepo.getInputSuspend()!![0]
            }
        }
        println(value.await())
    }
    class IncorrectBundleViewModelFactory(private val userRepo : UserInputRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(IncorrectBundleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return IncorrectBundleViewModel(userRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}