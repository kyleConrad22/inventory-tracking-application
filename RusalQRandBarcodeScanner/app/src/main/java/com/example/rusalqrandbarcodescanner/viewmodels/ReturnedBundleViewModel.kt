package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DelicateCoroutinesApi
class ReturnedBundleViewModel(private val codeRepo : CodeRepository, private val invRepo : CurrentInventoryRepository, private val userRepo : UserInputRepository) : ViewModel() {
    private var currentInput = UserInput(id = "null")
    private val currentLoadedBundles = codeRepo.allCodes.asLiveData()

    private var returnedBundle : CurrentInventoryLineItem? = null
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
            waitForInput()
            setUniqueList(currentInput.heatNum!!)
            setIsBundleLoadable(getHeat())
            loading.value = false
        }
    }

    fun getType() : String {
        return currentInput.type!!.lowercase()
    }

    fun getHeat() : String {
        return currentInput.heatNum!!
    }

    fun addBundle() = viewModelScope.launch {
        if (isBaseHeat(getHeat())) {
            invRepo.insert(returnedBundle!!)
        }
        val scannedCode = lineItemToScannedCode(returnedBundle!!)
        scannedCode.loadNum = currentInput.load
        scannedCode.loader = currentInput.loader
        scannedCode.workOrder = currentInput.order
        scannedCode.scanTime = getCurrentDateTime()
        codeRepo.insert(scannedCode)
    }

    private fun lineItemToScannedCode(currentInventoryLineItem : CurrentInventoryLineItem) : ScannedCode{
        return ScannedCode(
            heatNum = currentInventoryLineItem.heatNum,
            netWgtKg = currentInventoryLineItem.netWeightKg,
            grossWgtKg = currentInventoryLineItem.grossWeightKg,
            packageNum = currentInventoryLineItem.packageNum,
            bl = currentInventoryLineItem.blNum,
            quantity = currentInventoryLineItem.quantity,
            barCode = currentInventoryLineItem.barcode,
        )
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
                    val combo = listOf(code.blNum!!, code.quantity!!)
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
        if (!currentLoadedBundles.value.isNullOrEmpty()) {
            currentLoadedBundles.value!!.forEach {
                val heat = it.heatNum!!.substring(0, 6)
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
                            Scanned Bundles Heat is: ${getBaseHeat(getHeat())}
                            Heats Loaded are: 
                                ${loadedHeats[0]}
                                ${loadedHeats[1]}
                                ${loadedHeats[2]}
                        """.trimIndent()
                    }
                    isDuplicate -> {
                        reasoning = """
                            Bundle has already been added to ${getType()}!
                            Bundle was added at $scanTime.
                        """.trimIndent()
                    }
                    isIncorrectBl -> {
                        reasoning = """
                            Incorrect BL! The requested BL is ${currentInput.bl}, but the scanned BL is ${returnedBundle!!.blNum}!
                            Please load a different bundle.
                        """.trimIndent()
                    }
                    isIncorrectQuantity -> {
                        reasoning = """
                            Incorrect quantity! The requested quantity is ${currentInput.pieceCount}, but the scanned quantity is ${returnedBundle!!.quantity}.
                            Please load a different bundle.
                        """.trimIndent()
                    }
                    isIncorrectCombo -> {
                        reasoning = """
                            The requested heat returned multiple BL / quantity combinations, however, none of them contain the requested BL || Quantity combo of: ${currentInput.bl} || ${currentInput.pieceCount}
                        """.trimIndent()
                    }
                    else -> {
                        reasoning = """
                            Bundle ${getHeat()} could not be found in system! Please mark bundle and set aside! (If you are seeing this message in error please restart application or contact IT department.)
                        """.trimIndent()}
                }
            }
            isMultipleOptions -> {
                reasoning = """
                    Heat number is associated with multiple bl / quantity combinations! Please ensure that the BL is ${currentInput.bl} and the quantity is ${currentInput.pieceCount}!
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

    private suspend fun waitForInput() {
        val value = GlobalScope.async() {
            withContext(Dispatchers.Main) {
                currentInput = userRepo.getInputSuspend()!![0]
            }
        }
        value.await()
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
        if (!isIncorrectHeat && !isDuplicate) {
            setReturnedBundle(heat)
        }

        isIncorrectBundle = if (!isNotFound) {
            setIsIncorrectQuantity()
            setIsIncorrectBl()
            (isDuplicate || isIncorrectHeat || isIncorrectQuantity || isIncorrectBl)
        } else {
            true
        }
    }

    private suspend fun setReturnedBundle(heat: String) {
        if (isBaseHeat(heat)) {
            when {
                uniqueList.isEmpty() -> {
                    returnedBundle = null
                }

                uniqueList.size == 1 -> {
                    val repoData = invRepo.findByBaseHeat(heat)?.get(0)!!
                    returnedBundle = CurrentInventoryLineItem(heatNum = heat, blNum = repoData.blNum, quantity = repoData.quantity, grossWeightKg = "N/A", netWeightKg = "N/A", barcode = "${heat}u${getNumberOfUnidentifiedBundles(heat) + 1}")
                }

                uniqueList.size > 1 -> {
                    val bl = currentInput.bl
                    val quantity = currentInput.pieceCount
                    if (uniqueList.contains(listOf(bl, quantity))) {
                        isMultipleOptions = true
                        returnedBundle = CurrentInventoryLineItem(heatNum = heat, blNum = bl, quantity = quantity, grossWeightKg = "N/A", netWeightKg = "N/A", barcode = "${heat}u${getNumberOfUnidentifiedBundles(heat) + 1}")
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

    private suspend fun getNumberOfUnidentifiedBundles(heat : String) : Int {
        var result = 0
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                result = invRepo.findByBarcodes("${heat}u")!!.size
            }
        }
        value.await()
        return result
    }

    private fun setIsIncorrectBl() {
        isIncorrectBl = returnedBundle!!.blNum != currentInput.bl
    }

    private fun setIsIncorrectHeat(heat : String) {
        isIncorrectHeat = if (getType() == "load") {
            !getLoadedHeats().contains(heat) && getLoadedHeats().size == 3
        } else {
            false
        }
    }

    private fun setIsIncorrectQuantity() {
        isIncorrectQuantity = returnedBundle!!.quantity != currentInput.pieceCount
    }

    private suspend fun setIsDuplicate(heat : String) {
        val existingBundle = codeRepo.findByHeat(heat)
        if (existingBundle == null) {
            isDuplicate = false
        } else {
            isDuplicate = true
            scanTime = existingBundle.scanTime!!
        }
    }

    class ReturnedBundleViewModelFactory(private val codeRepo : CodeRepository, private val invRepo : CurrentInventoryRepository, private val userRepo : UserInputRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            @Suppress("UNCHECKED_CAST")
            if (modelClass.isAssignableFrom(ReturnedBundleViewModel::class.java)) {
                return ReturnedBundleViewModel(codeRepo, invRepo, userRepo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}