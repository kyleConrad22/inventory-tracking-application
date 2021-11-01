package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.Barge
import com.example.rusalqrandbarcodescanner.domain.models.Bl
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.util.inputvalidation.InputValidation
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

@DelicateCoroutinesApi
class InfoInputViewModel(private val mainActivityVM : MainActivityViewModel, private val invRepo : InventoryRepository) : ViewModel() {
    var displayConfirmButton by mutableStateOf(false)
    var displayButtons by mutableStateOf(true)

    val blList : MutableState<List<Bl>> = mutableStateOf(listOf())
    val bargeList : MutableState<List<Barge>> = mutableStateOf(listOf())

    val loading = mutableStateOf(false)

    var isValidLoad by mutableStateOf(true)
    var isValidLoader by mutableStateOf(true)
    var isValidChecker by mutableStateOf(true)
    var isValidOrder by mutableStateOf(true)
    var isValidPieceCount by mutableStateOf(true)
    var isValidQuantity by mutableStateOf(true)

    init {
        viewModelScope.launch {
            loading.value = true
            refresh()
            if (mainActivityVM.sessionType.value == SessionType.SHIPMENT) {
                blList.value = getUniqueBlList(invRepo.getAllSuspend()!!)
            } else {
                bargeList.value = getUniqueBargeList(invRepo.getAllSuspend()!!)
            }
            loading.value = false
        }
    }

    private fun getUniqueBargeList(items : List<RusalItem>) : List<Barge> {
        val result = mutableListOf<Barge>()
        for (item in items) {
            if (result.find { it.text == item.barge } == null) {
                result.add(Barge(item.barge))
            }
        }
        return result.toList()
    }

    private fun getUniqueBlList(items : List<RusalItem>) : List<Bl> {
        val result = mutableListOf<Bl>()
        for (item in items) {
            if (result.find {it.text == item.blNum } == null) {
                result.add(Bl(item.blNum))
            }
        }
        return result.toList()
    }

    fun refresh() {
        displayConfirmButton =
            if (mainActivityVM.sessionType.value == SessionType.SHIPMENT)
                "" !in listOf(
                    mainActivityVM.loadNum.value,
                    mainActivityVM.loader.value,
                    mainActivityVM.quantity.value,
                    mainActivityVM.bl.value,
                    mainActivityVM.pieceCount.value
                ) && mainActivityVM.workOrder.value.length == 9 && isValidLoad && isValidLoader && isValidOrder && isValidPieceCount

            else
                "" !in listOf(
                    mainActivityVM.barge.value,
                    mainActivityVM.checker.value
                )
    }

    fun validateLoad(input : String) {
        isValidLoad = InputValidation.lengthValidation(input, length = 2)
    }

    fun validateLoader(input : String) {
        isValidLoader = InputValidation.lengthValidation(input, length = 29)
    }

    fun validateChecker(input : String) {
        isValidChecker = InputValidation.lengthValidation(input, length = 29)
    }

    fun validateOrder(input : String) {
        isValidOrder = InputValidation.validateOrder(input)
    }

    fun validatePieceCount(input : String) {
        isValidPieceCount = InputValidation.lengthValidation(input, length = 2) && InputValidation.integerValidation(input)
    }

    fun validateQuantity(input : String) {
        isValidQuantity = InputValidation.integerValidation(input)
    }

    class InfoInputViewModelFactory(private val mainActivityVM : MainActivityViewModel, private val invRepo : InventoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(InfoInputViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InfoInputViewModel(mainActivityVM, invRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}