package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

@DelicateCoroutinesApi
class BundleInfoViewModel(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository) : ViewModel() {
    private val initCode : ScannedCode? = null
    private val initLoad : Boolean? = null
    val loading = mutableStateOf(false)
    val code = mutableStateOf(initCode)
    val isLoad = mutableStateOf(initLoad)

    fun setValues(barcode : String) {
        if (isLoad.value == null) {
            loading.value = true
            GlobalScope.launch(Dispatchers.Main) {
                setIsLoad()
                setCodeValue(barcode)
                loading.value = false
            }
        }
    }

    fun removeBundle() {
        loading.value = true
        GlobalScope.launch(Dispatchers.Main) {
            val value = GlobalScope.async {
                withContext(Dispatchers.Main) {
                    codeRepo.delete(code.value!!)
                }
            }
            println(value.await())
            loading.value = false
        }
    }

    fun resetViewModelState() {
        code.value = initCode
        isLoad.value = initLoad
    }

    private suspend fun setCodeValue(barcode : String) {
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                code.value = codeRepo.findByBarcode(barcode)
            }
        }
        println(value.await())
    }

    private suspend fun setIsLoad() {
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                val userInput = userRepo.getInputSuspend()!![0]
                isLoad.value = userInput.type == "Load"
            }
        }
        println(value.await())
    }

    class BundleInfoViewModelFactory(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(BundleInfoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return BundleInfoViewModel(userRepo, codeRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}