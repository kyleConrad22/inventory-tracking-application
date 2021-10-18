package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.ItemActionType
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.util.ScannedInfo
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DelicateCoroutinesApi
class ReturnedBundleViewModel(private val invRepo : InventoryRepository, private val mainActivityVM : MainActivityViewModel) : ViewModel() {

    private val heat = mainActivityVM.heatNum.value.replace("-","")

    val uniqueList = mutableStateOf(listOf<RusalItem>())
    val loading = mutableStateOf(true)
    private val canBeLoaded = mutableStateOf(false)
    val locatedItem : MutableState<RusalItem?> = mutableStateOf(null)
    val itemActionType = mutableStateOf(ItemActionType.INVALID_HEAT)

    init {
        loading.value = true

        viewModelScope.launch {

                if (isBaseHeat(heat)) {
                    useBaseHeatLogic()
                } else {
                    locatedItem.value = invRepo.findByHeat(heat)
                    Log.d("Debug", locatedItem.value.toString())
                }


                Log.d("DEBUG", itemActionType.value.type)

                itemActionType.value = when {
                    locatedItem.value == null -> ItemActionType.INVALID_HEAT

                    mainActivityVM.addedItems.value.find { it.heatNum == heat } != null -> ItemActionType.DUPLICATE

                    uniqueList.value.size > 1 -> ItemActionType.MULTIPLE_BLS_OR_PIECE_COUNTS

                    mainActivityVM.sessionType.value == SessionType.SHIPMENT -> getShipmentItemType(locatedItem.value!!)

                    else -> getReceptionItemType(locatedItem.value!!)
                }

                canBeLoaded.value = !(itemActionType.value in listOf(ItemActionType.INCORRECT_BL,
                    ItemActionType.DUPLICATE,
                    ItemActionType.INCORRECT_PIECE_COUNT,
                    ItemActionType.NOT_IN_LOADED_HEATS) || (itemActionType.value == ItemActionType.INVALID_HEAT && mainActivityVM.sessionType.value == SessionType.SHIPMENT))


            Log.d("DEBUG", "here")
            loading.value = false
            ScannedInfo.heatNum = ""
            Log.d("DEBUG", locatedItem.value!!.mark)
            Log.d("Debug", itemActionType.value.type)
        }
    }

    // Used to set parameters locatedItem, and unique list if necessary if the given heat is a base heat number
    private suspend fun useBaseHeatLogic() {
        val items = invRepo.findByBaseHeat(heat)

        if (items.isNullOrEmpty()) {
            locatedItem.value = null
        } else {

            uniqueList.value = getUniqueBlAndOptionCombos(items)
            if (uniqueList.value.size == 1) {
                val item = uniqueList.value[0]
                locatedItem.value = RusalItem(
                    heatNum = heat,
                    barcode = "${heat}u${getNumberOfUnidentifiedBundles(heat) + 1}",
                    blNum = item.blNum,
                    grade = item.grade,
                    mark = item.mark,
                    quantity = item.quantity,
                    dimension = item.dimension
                )
            } else {
                locatedItem.value = RusalItem(
                    heatNum = heat,
                    barcode = "${heat}u${getNumberOfUnidentifiedBundles(heat) + 1}",
                    blNum = mainActivityVM.bl.value,
                    quantity = mainActivityVM.pieceCount.value
                )
            }
        }
    }

    private fun getShipmentItemType(item : RusalItem) : ItemActionType {
        return when {
            item.blNum != mainActivityVM.bl.value -> ItemActionType.INCORRECT_BL
            item.quantity != mainActivityVM.pieceCount.value -> ItemActionType.INCORRECT_PIECE_COUNT
            getLoadedHeats().size == 3 && getLoadedHeats().find { it == item.heatNum } == null -> ItemActionType.NOT_IN_LOADED_HEATS
            else -> ItemActionType.VALID
        }
    }

    private fun getReceptionItemType(item : RusalItem) : ItemActionType {
        return when {
            item.barge != mainActivityVM.barge.value -> ItemActionType.INCORRECT_BARGE
            else -> ItemActionType.VALID
        }
    }

    fun getLoadedHeats() : List<String> {
        val result = mutableListOf<String>()
        mainActivityVM.addedItems.value.forEach { item ->
            if (result.find{ it == item.heatNum } == null) {
                result.add(item.heatNum)
            }
        }
        return result.toList()
    }

    fun isLastBundle(sessionType: SessionType) : Boolean {
        if (sessionType == SessionType.SHIPMENT) {
            val requestedQuantity = mainActivityVM.quantity.value.toInt()
            return requestedQuantity - mainActivityVM.addedItemCount.value == 1
        }

        return false
    }

    fun addItem() {
        loading.value = true
        viewModelScope.launch {
            if (isBaseHeat(heat)) {
                invRepo.insert(locatedItem.value!!)
            }

            invRepo.updateIsAddedStatus(true, heat)

            if (mainActivityVM.sessionType.value == SessionType.SHIPMENT) {
                invRepo.updateShipmentFields(
                    mainActivityVM.workOrder.value,
                    mainActivityVM.loadNum.value,
                    mainActivityVM.loader.value,
                    getCurrentDateTime(),
                    heat)
            } else {
                invRepo.updateReceptionFields(
                    getCurrentDate(),
                    mainActivityVM.checker.value,
                    heat
                )
            }
            mainActivityVM.refresh()
            mainActivityVM.heatNum.value = ""
            loading.value = false
        }
    }

    private fun getCurrentDate() : String {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
        return LocalDateTime.now().format(formatter)
    }

    private fun getCurrentDateTime() : String {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
        return LocalDateTime.now().format(formatter)
    }

    private fun getUniqueBlAndOptionCombos(items : List<RusalItem>) : List<RusalItem> {
        val uniqueList = mutableListOf<RusalItem>()
        for (item in items) {
            if (uniqueList.find { it.blNum == item.blNum || it.quantity == item.quantity} == null) {
                uniqueList.add(item)
            }
        }
        return uniqueList
    }

    private fun isBaseHeat(heat : String) : Boolean {
        return heat.length == 6
    }

    private suspend fun getNumberOfUnidentifiedBundles(heat : String) : Int {
        var result = 0
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                val repoData = invRepo.findByBarcodes("${heat}u")
                result = if (repoData.isNullOrEmpty()) {
                    0
                } else {
                    repoData.size
                }
            }
        }
        value.await()
        return result
    }

    class ReturnedBundleViewModelFactory(private val invRepo : InventoryRepository, private val mainActivityVM : MainActivityViewModel) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(ReturnedBundleViewModel::class.java)) {
                return ReturnedBundleViewModel(invRepo, mainActivityVM) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}