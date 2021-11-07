package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.repositories.InventoryRepository
import com.example.rusalqrandbarcodescanner.util.getCurrentDateTime
import com.example.rusalqrandbarcodescanner.util.inputvalidation.InputValidation
import com.example.rusalqrandbarcodescanner.util.isBaseHeat
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class NewItemViewModel(private val mainActivityVM : MainActivityViewModel, private val repo : InventoryRepository) : ViewModel() {

    var heat = mainActivityVM.heatNum.value
    var scannedItem = RusalItem(barcode = "") // Item returned from scanner and/or with values set through manual entry

    val scannedItemGrade = mutableStateOf(scannedItem.grade)
    val scannedItemGrossWeight = mutableStateOf(scannedItem.grossWeightKg)
    val scannedItemNetWeight = mutableStateOf(scannedItem.netWeightKg)
    val scannedItemQuantity = mutableStateOf(scannedItem.quantity)
    val scannedItemMark = mutableStateOf("")
    val isConfirmAdditionVisible = mutableStateOf(false)

    var isValidGrossWeight by mutableStateOf(true)
    var isValidNetWeight by mutableStateOf(true)
    var isValidQuantity by mutableStateOf(true)
    var isValidMark by mutableStateOf(true)

    init {
        trySettingItemValuesViaScan()
    }

    // Sets scannedItem value if item was returned through a QR scan rather than through manual entry
    internal fun trySettingItemValuesViaScan() {
        if (mainActivityVM.scannedItem.barcode != "") {
            scannedItem = mainActivityVM.scannedItem
            scannedItemGrade.value = mainActivityVM.scannedItem.grade
            scannedItemQuantity.value = mainActivityVM.scannedItem.quantity
            scannedItemGrossWeight.value = mainActivityVM.scannedItem.grossWeightKg
            scannedItemNetWeight.value = mainActivityVM.scannedItem.netWeightKg
            refresh()
        }
    }

    // Checks if the confirm addition button should be visible, setting visibility as necessary
    internal fun refresh() {
        isConfirmAdditionVisible.value =
            "" !in listOf(scannedItemGrade.value, scannedItemGrossWeight.value, scannedItemNetWeight.value, scannedItemQuantity.value, scannedItemMark.value)
                    && isValidGrossWeight && isValidNetWeight && isValidMark && isValidQuantity
    }

    // Adds new item to inventory if being added through reception as a new item
    internal fun receiveNewItem() = viewModelScope.launch() {
        if (scannedItem.barcode == "") {
            scannedItem = RusalItem(
                heatNum = heat,
                grossWeightKg = scannedItemGrossWeight.value,
                netWeightKg = scannedItemNetWeight.value,
                grade = scannedItemGrade.value,
                quantity = scannedItemQuantity.value,
                barcode =
                if (isBaseHeat(heat)) "${heat}u${getNumberOfUnidentifiedBundles(heat) + 1}"
                else "${heat}n",
            )
        }
        scannedItem.blNum = "N/A"
        scannedItem.mark = scannedItemMark.value
        scannedItem.barge = mainActivityVM.barge.value
        scannedItem.lot = "N/A"
        addItem(scannedItem)
    }

    internal suspend fun getNumberOfUnidentifiedBundles(heat : String) : Int {
        val repoData = repo.findByBarcodes("${heat}u")

        return if (repoData.isNullOrEmpty()) 0
        else repoData.size

    }

    fun validateGrossWeight(input : String) {
        isValidGrossWeight = InputValidation.lengthValidation(input, 4) && InputValidation.integerValidation(input)
    }

    fun validateNetWeight(input : String) {
        isValidNetWeight = InputValidation.lengthValidation(input, 4) && InputValidation.integerValidation(input)
    }

    fun validateQuantity(input : String) {
        isValidQuantity = InputValidation.lengthValidation(input, 2) && InputValidation.integerValidation(input)
    }

    fun validateMark(input : String) {
        isValidMark = InputValidation.lengthValidation(input, 29)
    }

    internal fun addItem(item : RusalItem) = viewModelScope.launch {
        repo.insert(item)
        repo.updateIsAddedStatusViaBarcode(true, item.barcode)
        repo.updateReceptionFieldsViaBarcode(getCurrentDateTime(), mainActivityVM.checker.value, item.barcode)
        mainActivityVM.scannedItem = RusalItem(barcode = "")
        mainActivityVM.refresh()
    }

    class NewItemViewModelFactory(private val mainActivityVM: MainActivityViewModel, private val repo : InventoryRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NewItemViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NewItemViewModel(mainActivityVM, repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

    companion object {
        private const val TAG = "NewItemViewModel"
    }
}