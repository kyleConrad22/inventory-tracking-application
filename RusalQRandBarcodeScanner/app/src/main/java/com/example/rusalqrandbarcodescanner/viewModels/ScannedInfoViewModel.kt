package com.example.rusalqrandbarcodescanner.viewModels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.rusalqrandbarcodescanner.ScannedInfo
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class ScannedInfoViewModel(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository) : ViewModel() {
    private val init : Boolean? = null
    private var userInput: UserInput? = null

    val heatNum = ScannedInfo.heatNum
    val blNum = ScannedInfo.blNum
    val loading  = mutableStateOf(true)
    val isLoad = mutableStateOf(init)

    fun setIsLoad() {
        if (isLoad.value == null) {
            GlobalScope.launch(Dispatchers.Main) {
                getInput()
                isLoad.value = userInput!!.type == "Load"
                loading.value = false
            }
        }
    }

    private suspend fun getInput() {
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                userInput = userRepo.getInputSuspend()!![0]
            }
        }
        println(value.await())
    }

    fun clearValues() {
        ScannedInfo.clearValues()
    }

    fun addBundle() {
        loading.value = true
        GlobalScope.launch(Dispatchers.Main) {

        }
    }

    private suspend fun insert() {
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                val code = ScannedCode(
                    barCode = ScannedInfo.barCode,
                    loader = userInput!!.loader,
                    loadNum = userInput!!.load,
                    bl = ScannedInfo.blNum,
                    netWgtLbs = ScannedInfo.netWgtLbs,
                    netWgtKg = ScannedInfo.netWgtKg,
                    grossWgtLbs = ScannedInfo.grossWgtLbs,
                    grossWgtKg = ScannedInfo.grossWgtKg,
                    workOrder = userInput!!.order,
                    scanTime = ScannedInfo.scanTime,
                    quantity = ScannedInfo.quantity,
                    heatNum = ScannedInfo.heatNum,
                    packageNum = ScannedInfo.packageNum
                )
                codeRepo.insert(code)
            }
        }
        println(value.await())
        loading.value = false
    }

    class ScannedInfoViewModelFactory(private val userRepo : UserInputRepository, private val codeRepo : CodeRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) : T {
            if (modelClass.isAssignableFrom(ScannedInfoViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ScannedInfoViewModel(userRepo, codeRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}