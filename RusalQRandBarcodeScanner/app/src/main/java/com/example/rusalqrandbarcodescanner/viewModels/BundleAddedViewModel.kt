package com.example.rusalqrandbarcodescanner.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rusalqrandbarcodescanner.util.ScannedInfo
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class BundleAddedViewModel(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository) : ViewModel() {
    private val init : Boolean? = null

    val loading = mutableStateOf(false)
    val isLoad = mutableStateOf(init)
    val destination = mutableStateOf(Screen.ToBeImplementedScreen.title)
    val bundlesRemaining = mutableStateOf(0)

    fun setValues() {
        if (isLoad.value == null) {
            GlobalScope.launch(Dispatchers.Main) {
                val userInput = getInput()
                setDestination(userInput!!)
                setIsLoad(userInput)
                loading.value = false
            }
        }
    }

    private fun setIsLoad(userInput : UserInput) {
        isLoad.value = when (userInput.type) {
            "Load" -> {
                true
            }
            "Reception" -> {
                false
            }
            else -> {
                null
            }
        }
    }

    private suspend fun getBundlesRemaining(userInput: UserInput) {
        val count = getCount()
        bundlesRemaining.value = Integer.parseInt(userInput.bundleQuantity!!) - count!!
    }

    private suspend fun setDestination(userInput : UserInput) {
        getBundlesRemaining(userInput)
        if (bundlesRemaining.value == 0) {
            destination.value = Screen.ReviewScreen.title
        } else {
            ScannedInfo.clearValues()
            destination.value = Screen.ScannerScreen.title
        }
    }

    private suspend fun getInput() : UserInput? {
        var result : UserInput? = null
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                result = userRepo.getInputSuspend()!![0]
            }
        }
        println(value.await())
        return result
    }

    private suspend fun getCount() : Int? {
        var result : Int? = null
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                result = codeRepo.getRowCount()
            }
        }
        println(value.await())
        return result
    }


    class BundleAddedViewModelFactory(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(BundleAddedViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BundleAddedViewModel(userRepo, codeRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}