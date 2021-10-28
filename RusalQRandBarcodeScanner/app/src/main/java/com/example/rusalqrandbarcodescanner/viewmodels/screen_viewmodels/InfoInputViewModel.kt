package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.Barge
import com.example.rusalqrandbarcodescanner.domain.models.Bl
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

@DelicateCoroutinesApi
class InfoInputViewModel(private val mainActivityVM : MainActivityViewModel, private val invRepo : InventoryRepository) : ViewModel() {
    val displayConfirmButton = mutableStateOf(false)

    val uiState = mutableStateOf<InfoInputState>(InfoInputState.Editable)

    val blList : MutableState<List<Bl>> = mutableStateOf(listOf())
    val bargeList : MutableState<List<Barge>> = mutableStateOf(listOf())

    val loading = mutableStateOf(false)

    init {
        viewModelScope.launch {
            loading.value = true
            refresh()
            if (mainActivityVM.addedItemCount.value != 0) {
                uiState.value = InfoInputState.Uneditable
            }
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
        val valueList = if (mainActivityVM.sessionType.value == SessionType.SHIPMENT) {
            listOf(
                mainActivityVM.workOrder.value,
                mainActivityVM.loadNum.value,
                mainActivityVM.loader.value,
                mainActivityVM.quantity.value,
                mainActivityVM.bl.value,
                mainActivityVM.pieceCount.value
            )
        } else {
            listOf(
                mainActivityVM.barge.value,
                mainActivityVM.checker.value
            )
        }
        displayConfirmButton.value = !valueList.contains("")
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

    sealed class InfoInputState {
        object Editable : InfoInputState()
        object Uneditable : InfoInputState()
    }
}