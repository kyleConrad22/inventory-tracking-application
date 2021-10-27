package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.ItemActionType
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.util.Commodity
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ReturnedItemViewModel(private val invRepo : InventoryRepository, private val mainActivityVM : MainActivityViewModel) : ViewModel() {

    private val heat = mainActivityVM.heatNum.value
    private var scannedItem = RusalItem(barcode = "") // Item returned from scanner and/or with values set through manual entry

    val uniqueList = mutableStateOf(listOf<RusalItem>())
    val loadedHeats = mutableStateOf(listOf<String>())
    val loading = mutableStateOf(true)
    val locatedItem : MutableState<RusalItem?> = mutableStateOf(null) // Item returned from database
    val itemActionType = mutableStateOf(ItemActionType.INVALID_HEAT)

    val scannedItemGrade = mutableStateOf(scannedItem.grade)
    val scannedItemGrossWeight = mutableStateOf(scannedItem.grossWeightKg)
    val scannedItemNetWeight = mutableStateOf(scannedItem.netWeightKg)
    val scannedItemQuantity = mutableStateOf(scannedItem.quantity)
    val scannedItemMark = mutableStateOf("")
    val isConfirmAdditionVisible = mutableStateOf(false)

    // Sets the state of ReturnedItem Screen on load
    init {
        loading.value = true

        viewModelScope.launch {

                if (isBaseHeat(heat)) {
                    useBaseHeatLogic()
                } else {
                    locatedItem.value = invRepo.findByHeat(heat)
                }

                itemActionType.value = when {
                    locatedItem.value == null -> ItemActionType.INVALID_HEAT

                    mainActivityVM.addedItems.value.find { it.heatNum == heat } != null || (mainActivityVM.sessionType.value == SessionType.RECEPTION && locatedItem.value!!.receptionDate != "") -> ItemActionType.DUPLICATE

                    uniqueList.value.size > 1 -> ItemActionType.MULTIPLE_BLS_OR_PIECE_COUNTS

                    mainActivityVM.sessionType.value == SessionType.SHIPMENT -> getShipmentItemType(locatedItem.value!!)

                    else -> getReceptionItemType(locatedItem.value!!)
                }

            // Sets scannedItem value if item was returned through a QR scan rather than through manual entry
            if (itemActionType.value == ItemActionType.INVALID_HEAT && mainActivityVM.sessionType.value == SessionType.RECEPTION && mainActivityVM.scannedItem.barcode != "") {
                scannedItem = mainActivityVM.scannedItem
                scannedItemGrade.value = mainActivityVM.scannedItem.grade
                scannedItemQuantity.value = mainActivityVM.scannedItem.quantity
                scannedItemGrossWeight.value = mainActivityVM.scannedItem.grossWeightKg
                scannedItemNetWeight.value = mainActivityVM.scannedItem.netWeightKg
                refresh()
            }
            loading.value = false
        }
    }

    // Checks if the confirm addition button should be visible, setting visibility as necessary
    fun refresh() {
        isConfirmAdditionVisible.value = "" !in listOf(scannedItemGrade.value, scannedItemGrossWeight.value, scannedItemNetWeight.value, scannedItemQuantity.value, scannedItemMark.value)
    }

    // Used to set parameters {locatedItem, and unique list (if necessary)} if the given heat is a base heat number
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

    // Gets the item type of an item given that the session is a shipment
    private suspend fun getShipmentItemType(item : RusalItem) : ItemActionType {
        return when {
            item.blNum != mainActivityVM.bl.value -> ItemActionType.INCORRECT_BL
            item.quantity != mainActivityVM.pieceCount.value -> ItemActionType.INCORRECT_PIECE_COUNT
            getLoadedHeats().size == 3 && getLoadedHeats().find { it == item.heatNum } == null -> ItemActionType.NOT_IN_LOADED_HEATS
            else -> ItemActionType.VALID
        }
    }

    // Gets the item type of an item given that the session is a reception
    private fun getReceptionItemType(item : RusalItem) : ItemActionType {
        return when {
            item.barge != mainActivityVM.barge.value -> ItemActionType.INCORRECT_BARGE
            else -> ItemActionType.VALID
        }
    }

    // Gets the list of all base heats currently added to the session
    private suspend fun getLoadedHeats() : List<String> {
        val result = mutableListOf<String>()
        if (getCommodity(invRepo.findByBl(mainActivityVM.bl.value)) == Commodity.INGOTS) {
            mainActivityVM.addedItems.value.forEach { item ->
                if (result.find { it == item.heatNum } == null) {
                    result.add(item.heatNum)
                }
            }
        }
        loadedHeats.value = result.toList()
        return result.toList()
    }

    private fun getCommodity(items : List<RusalItem>) : Commodity {
        if (items[0].grade.contains("INGOTS")) {
            return Commodity.INGOTS
        }
        return Commodity.BILLETS
    }

    fun isLastItem(sessionType: SessionType) : Boolean {
        if (sessionType == SessionType.SHIPMENT) {
            val requestedQuantity = mainActivityVM.quantity.value.toInt()
            return requestedQuantity - mainActivityVM.addedItemCount.value == 0
        }

        return false
    }

    // Adds item to current session using appropriate logic based on session type, if item is not null then will add item to inventory first
    fun addItem(item : RusalItem? = null) {
        loading.value = true
        viewModelScope.launch {
            if (item != null) {
                invRepo.insert(item)
            } else if (isBaseHeat(heat)) {
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
            mainActivityVM.scannedItem = RusalItem(barcode = "")
            mainActivityVM.refresh()
            loading.value = false
        }
    }

    // Adds new item to inventory if being added through reception as a new item
    fun receiveNewItem() {
        if (scannedItem.barcode == "") {
            scannedItem = RusalItem(
                heatNum = heat,
                grossWeightKg = scannedItemGrossWeight.value,
                netWeightKg = scannedItemNetWeight.value,
                grade = scannedItemGrade.value,
                quantity = scannedItemQuantity.value,
                barcode = "${heat}new",
            )
        }
        scannedItem.mark = scannedItemMark.value
        scannedItem.barge = mainActivityVM.barge.value
        scannedItem.lot = "N/A"
        addItem(scannedItem)
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

    // Determines if the given heat is a base heat
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

    class ReturnedItemViewModelFactory(private val invRepo : InventoryRepository, private val mainActivityVM : MainActivityViewModel) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(ReturnedItemViewModel::class.java)) {
                return ReturnedItemViewModel(invRepo, mainActivityVM) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        private const val TAG = "ReturnedItemViewModel"
    }
}