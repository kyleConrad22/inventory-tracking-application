package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.domain.models.Bl
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

@DelicateCoroutinesApi
class InfoInputViewModel(private val userRepo : UserInputRepository, private val invRepo : InventoryRepository) : ViewModel() {
    val isConfirmVis = mutableStateOf(false)
    val isLoad = mutableStateOf(false)

    val blList : MutableState<List<Bl>> = mutableStateOf(listOf())
    val loader = mutableStateOf("")
    val order = mutableStateOf("")
    val load = mutableStateOf("")
    val bundles = mutableStateOf("")
    val bl = mutableStateOf("")
    val vessel = mutableStateOf("")
    val checker = mutableStateOf("")
    val quantity = mutableStateOf("")

    val loading = mutableStateOf(false)

    init {
        viewModelScope.launch {
            loading.value = true
            val result = invRepo.getAllSuspend()
            blList.value = getUniqueBlList(result!!)
            isLoad.value = userRepo.getInputSuspend()!![0].type == "Load"
            loading.value = false
        }
    }

    private fun getUniqueBlList(lineItems : List<RusalItem>) : List<Bl> {
        val result = mutableListOf<Bl>()
        for (lineItem in lineItems) {
            if (result.find {it.blNumber == lineItem.blNum } == null) {
                result.add(Bl(lineItem.blNum))
            }
        }
        return result.toList()
    }

    fun getUpdate() {
        loading.value = true
        GlobalScope.launch(Dispatchers.Main) {
            update()
        }
    }

    private suspend fun update() {
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                val type = if (isLoad.value) { "Load" } else { "Reception" }

                val userInput = UserInput(
                    id = "data",
                    order = order.value,
                    load = load.value,
                    loader = loader.value,
                    bundleQuantity = bundles.value,
                    bl = bl.value,
                    pieceCount = quantity.value,
                    checker = checker.value,
                    vessel = vessel.value,
                    heatNum = "",
                    type = type
                )
                userRepo.update(userInput)
            }
        }
        value.await()
        loading.value = false
    }

    fun refresh() {
        val valueList = if (isLoad.value) {
            listOf(order.value,
                load.value,
                loader.value,
                bundles.value,
                bl.value,
                quantity.value)
        } else {
            listOf(vessel.value, checker.value)
        }
        isConfirmVis.value = !valueList.contains("")
    }

    class InfoInputViewModelFactory(private val userRepo : UserInputRepository, private val invRepo : InventoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(InfoInputViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InfoInputViewModel(userRepo, invRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}