package com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.RusalItem
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
    private val currentLoadedBundles : MutableState<List<RusalItem>> = mutableStateOf(listOf())

    private var returnedBundle : RusalItem? = null
    private var isIncorrectHeat = false
    private var isDuplicate = false
    private var isIncorrectBl = false
    private var isIncorrectQuantity = false
    private var isNotFound = false
    private var scanTime = ""
    private var isIncorrectCombo = false

    var uniqueList = listOf<List<String>>()
    val loading = mutableStateOf(false)
    var isIncorrectBundle = false
    var isMultipleOptions = false
    var reasoning  = ""

    init {
        loading.value = true
        GlobalScope.launch {
            currentLoadedBundles.value = invRepo.getAddedItems()
            setUniqueList(mainActivityVM.heatNum.value)
            setIsBundleLoadable(mainActivityVM.heatNum.value)
            loading.value = false
            ScannedInfo.heatNum = ""
        }
    }

    fun isLastBundle() : Boolean {
        val requestedQuantity = mainActivityVM.quantity.value.toInt()
        return requestedQuantity - mainActivityVM.addedItemCount.value == 1
    }

    fun addBundle() {
        loading.value = true
        viewModelScope.launch {
            if (isBaseHeat(mainActivityVM.heatNum.value)) {
                invRepo.insert(returnedBundle!!)
            }

            invRepo.updateIsAddedStatus(true, mainActivityVM.heatNum.value)

            if (mainActivityVM.sessionType.value == SessionType.SHIPMENT) {
                invRepo.updateLoadFields(mainActivityVM.workOrder.value,
                    mainActivityVM.workOrder.value,
                    mainActivityVM.loader.value,
                    getCurrentDateTime(),
                    mainActivityVM.heatNum.value)
            }
            mainActivityVM.refresh()
            mainActivityVM.heatNum.value = ""
            loading.value = false
        }
    }

    private fun getCurrentDateTime() : String {
        val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
        return LocalDateTime.now().format(formatter)
    }

    private suspend fun setUniqueList(heat : String) {
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                val uniqueCodes = mutableListOf<List<String>>()
                for (code in invRepo.findByBaseHeat(heat)!!) {
                    val combo = listOf(code.blNum, code.quantity)
                    if (!uniqueCodes.contains(combo)) {
                        uniqueCodes.add(combo)
                    }
                }
                uniqueList = uniqueCodes.toList()
            }
        }
        value.await()
    }

    private fun getBaseHeat(heat : String) : String {
        return if (isBaseHeat(heat)) {
            heat
        } else {
            heat.substring(0, heat.length - 2)
        }
    }

    private fun isBaseHeat(heat : String) : Boolean {
        return heat.length == 6
    }

    private fun getLoadedHeats() : List<String> {
        val heatList : MutableList<String> = mutableListOf()
        if (!mainActivityVM.addedItems.value.isNullOrEmpty()) {
            currentLoadedBundles.value.forEach {
                val heat = it.heatNum.substring(0, 6)
                if (!heatList.contains(heat)) {
                    heatList.add(heat)
                }
            }
        }
        return heatList.toList()
    }

    private fun setReasoning() {

        when {
            isIncorrectBundle-> {
                when {
                    isIncorrectHeat -> {
                        val loadedHeats = getLoadedHeats()
                        reasoning = """
                            Incorrect heat! Ingot loads may only have three unique heats!
                            Scanned Bundle's Heat is: ${getBaseHeat(mainActivityVM.heatNum.value)}
                            Heats Loaded are: 
                                ${loadedHeats[0]}
                                ${loadedHeats[1]}
                                ${loadedHeats[2]}
                        """.trimIndent()
                    }
                    isDuplicate -> {
                        reasoning = """
                            Bundle has already been added to ${mainActivityVM.sessionType.value.type}!
                            Bundle was added at $scanTime.
                        """.trimIndent()
                    }
                    isIncorrectBl -> {
                        reasoning = """
                            Incorrect BL! The requested BL is ${mainActivityVM.bl.value}, but the scanned BL is ${returnedBundle!!.blNum}!
                            Please load a different bundle.
                        """.trimIndent()
                    }
                    isIncorrectQuantity -> {
                        reasoning = """
                            Incorrect quantity! The requested quantity is ${mainActivityVM.pieceCount.value}, but the scanned quantity is ${returnedBundle!!.quantity}.
                            Please load a different bundle.
                        """.trimIndent()
                    }
                    isIncorrectCombo -> {
                        reasoning = """
                            The requested heat returned multiple BL / quantity combinations, however, none of them contain the requested BL || Quantity combo of: ${mainActivityVM.bl.value} || ${mainActivityVM.pieceCount.value}
                        """.trimIndent()
                    }
                    else -> {
                        reasoning = """
                            Bundle ${mainActivityVM.heatNum.value} could not be found in system! Please mark bundle and set aside! (If you are seeing this message in error please restart application or contact IT department.)
                        """.trimIndent()}
                }
            }
            isMultipleOptions -> {
                reasoning = """
                    Heat number is associated with multiple bl / quantity combinations! Please ensure that the BL is ${mainActivityVM.bl.value} and the quantity is ${mainActivityVM.pieceCount.value}!
                    Returned identifiers:
                """.trimIndent()
            }
            else -> {
                reasoning = """
                    Heat Number: ${returnedBundle!!.heatNum}
                    Bl: ${returnedBundle!!.blNum}
                    Quantity: ${returnedBundle!!.quantity}
                    Net Weight Kg: ${returnedBundle!!.netWeightKg}
                    Gross Weight Kg: ${returnedBundle!!.grossWeightKg}
                    Barcode : ${returnedBundle!!.barcode}
                """.trimIndent()
            }
        }
    }

    private suspend fun setIsBundleLoadable(heat : String) {
        val value = GlobalScope.async() {
            withContext(Dispatchers.Main) {
                setIsIncorrectBundle(heat)
                setReasoning()
            }
        }
        value.await()
    }

    private suspend fun setIsIncorrectBundle(heat : String) {
        setIsIncorrectHeat(getBaseHeat(heat))
        setIsDuplicate(heat)
        println("IncorrectHeat: ${isIncorrectHeat}\nDuplicate: $isDuplicate")
        if (!isIncorrectHeat && !isDuplicate) {
            setReturnedBundle(heat)
            setIsIncorrectQuantity()
            setIsIncorrectBl()
        }

        isIncorrectBundle = if (!isNotFound) {
            (isDuplicate || isIncorrectHeat || isIncorrectQuantity || isIncorrectBl)
        } else {
            true
        }
    }

    private suspend fun setReturnedBundle(heat: String) {
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                if (isBaseHeat(heat)) {
                    when {
                        uniqueList.isEmpty() -> {
                            returnedBundle = null
                        }

                        uniqueList.size == 1 -> {
                            val repoData = invRepo.findByBaseHeat(heat)?.get(0)!!
                            returnedBundle = RusalItem(heatNum = heat, blNum = repoData.blNum, quantity = repoData.quantity, grossWeightKg = "N/A", netWeightKg = "N/A", barcode = "${heat}u${getNumberOfUnidentifiedBundles(heat) + 1}")
                        }

                        uniqueList.size > 1 -> {
                            val bl = mainActivityVM.bl.value
                            val quantity = mainActivityVM.pieceCount.value
                            if (uniqueList.contains(listOf(bl, quantity))) {
                                isMultipleOptions = true
                                returnedBundle = RusalItem(heatNum = heat, blNum = bl, quantity = quantity, grossWeightKg = "N/A", netWeightKg = "N/A", barcode = "${heat}u${getNumberOfUnidentifiedBundles(heat) + 1}")
                            } else {
                                isIncorrectBundle = true
                                isIncorrectCombo = true
                            }
                        }
                    }
                } else {
                    returnedBundle = invRepo.findByHeat(heat)
                }
                isIncorrectBundle = returnedBundle == null
                isNotFound = isIncorrectBundle
            }
        }
        println(value.await())
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

    private fun setIsIncorrectBl() {
        isIncorrectBl = returnedBundle!!.blNum != mainActivityVM.bl.value
    }

    private fun setIsIncorrectHeat(heat : String) {
        isIncorrectHeat = if (mainActivityVM.sessionType.value == SessionType.SHIPMENT) {
            !getLoadedHeats().contains(heat) && getLoadedHeats().size == 3
        } else {
            false
        }
    }

    private fun setIsIncorrectQuantity() {
        isIncorrectQuantity = returnedBundle!!.quantity != mainActivityVM.pieceCount.value
    }

    private suspend fun setIsDuplicate(heat : String) {
        if (!isBaseHeat(heat)) {
            val returnedItem = invRepo.findByHeat(heat)
            if (returnedItem != null && returnedItem.isAdded) {
                isDuplicate = true
                scanTime = returnedItem.loadTime
            } else {
                isDuplicate = false
            }
        } else {
            isDuplicate = false
        }
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