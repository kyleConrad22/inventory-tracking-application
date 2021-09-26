package com.example.rusalqrandbarcodescanner.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DelicateCoroutinesApi
class ReturnedBundleViewModel(private val codeRepo : CodeRepository, private val invRepo : CurrentInventoryRepository, private val userRepo : UserInputRepository) : ViewModel() {
    private val currentInput = userRepo.currentInput.asLiveData()
    private val currentLoadedBundles = codeRepo.allCodes.asLiveData()

    private var returnedBundle : CurrentInventoryLineItem? = null
    private var isIncorrectHeat = false
    private var isDuplicate = false
    private var isIncorrectBl = false
    private var isIncorrectQuantity = false
    private var isNotFound = false
    private var scanTime = ""
    var uniqueList = listOf<List<String>>()

    val loading = mutableStateOf(false)
    val isIncorrectBundle = mutableStateOf(false)
    val isMultipleOptions = mutableStateOf(false)
    val reasoning  = mutableStateOf("")

    fun initialize() {
        waitForUserInputLiveData()
        if (!loading.value && getHeat().length > 6) {
            loading.value = true
            GlobalScope.launch {
                withContext(Dispatchers.Main) {
                    setIsBundleLoadable(getHeat())
                }
            }
            loading.value = false
        }
    }

    fun getType() : String {
        return currentInput.value!![0].type!!.lowercase()
    }

    fun getHeat() : String {
        return currentInput.value!![0].heatNum!!
    }

    fun addBundle() = viewModelScope.launch {
        if (isBaseHeat(getHeat())) {
            invRepo.insert(returnedBundle!!)
        }
        val scannedCode = lineItemToScannedCode(returnedBundle!!)
        scannedCode.loadNum = currentInput.value!![0].load
        scannedCode.loader = currentInput.value!![0].loader
        scannedCode.workOrder = currentInput.value!![0].order
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
        val uniqueCodes = mutableListOf<List<String>>()
        for (code in invRepo.findByBaseHeat(heat)!!) {
            val combo = listOf(code.blNum!!, code.quantity!!)
            if (!uniqueCodes.contains(combo)) {
                uniqueCodes.add(combo)
            }
        }
        uniqueList = uniqueCodes.toList()
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
        currentLoadedBundles.value!!.forEach {
            val heat = it.heatNum!!.substring(0, 6)
            if (!heatList.contains(heat)) {
                heatList.add(heat)
            }
        }
        return heatList.toList()
    }

    private fun setReasoning() {

        when {
            isIncorrectBundle.value -> {
                when {
                    isIncorrectHeat -> {
                        val loadedHeats = getLoadedHeats()
                        reasoning.value = """
                            Incorrect heat! Ingot loads may only have three unique heats!
                            Scanned Bundles Heat is: ${getBaseHeat(getHeat())}
                            Heats Loaded are: 
                                ${loadedHeats[0]}
                                ${loadedHeats[1]}
                                ${loadedHeats[2]}
                        """.trimIndent()
                    }
                    isDuplicate -> {
                        reasoning.value = """
                            Bundle has already been added to ${getType()}!
                            Bundle was added at $scanTime.
                        """.trimIndent()
                    }
                    isIncorrectBl -> {
                        reasoning.value = """
                            Incorrect BL! The requested BL is ${currentInput.value!![0].bl}, but the scanned BL is ${returnedBundle!!.blNum}!
                            Please load a different bundle.
                        """.trimIndent()
                    }
                    isIncorrectQuantity -> {
                        reasoning.value = """
                            Incorrect quantity! The requested quantity is ${currentInput.value!![0].bundleQuantity}, but the scanned quantity is ${returnedBundle!!.quantity}.
                            Please load a different bundle.
                        """.trimIndent()
                    }
                    else -> {
                        reasoning.value = """
                            Bundle ${getHeat()} could not be found in system! Please mark bundle and set aside! (If you are seeing this message in error please restart application or contact IT department.)
                        """.trimIndent()}
                }
            }
            isMultipleOptions.value -> {
                reasoning.value = """
                    Heat number is associated with multiple bl / quantity combinations! Please ensure that the BL is ${currentInput.value!![0].bl} and the quantity is ${currentInput.value!![0].bundleQuantity}!
                    Returned identifiers:
                """.trimIndent()
            }
            else -> {
                reasoning.value = """
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
        val value = GlobalScope.async(Dispatchers.Main) {
            setIsIncorrectBundle(heat)
            setReasoning()
        }
        println(value.await())
    }

    private suspend fun setIsIncorrectBundle(heat : String) {
        setIsIncorrectHeat(getBaseHeat(heat))
        setIsDuplicate(heat)
        if (!isIncorrectHeat && !isDuplicate) {
            setReturnedBundle(heat)
        }

        if (!isNotFound) {
            setIsIncorrectQuantity()
            setIsIncorrectBl()
            isIncorrectBundle.value = (isDuplicate || isIncorrectHeat || isIncorrectQuantity || isIncorrectBl)
        } else {
            isIncorrectBundle.value = true
        }
    }

    private suspend fun setReturnedBundle(heat: String) {
        if (isBaseHeat(heat)) {
            setUniqueList(heat)
            when {
                uniqueList.isEmpty() -> {
                    returnedBundle = null
                }

                uniqueList.size == 1 -> {
                    returnedBundle = invRepo.findByBaseHeat(heat)?.get(0)
                }

                uniqueList.size > 1 -> {
                    val bl = currentInput.value?.get(0)!!.bl
                    val quantity = currentInput.value?.get(0)!!.bundleQuantity
                    if (uniqueList.contains(listOf(bl, quantity))) {
                        isMultipleOptions.value = true
                        returnedBundle = CurrentInventoryLineItem(heatNum = heat, blNum = bl, quantity = quantity, barcode = "${heat}u${getNumberOfUnidentifiedBundles(heat) + 1}")
                    } else {
                        isIncorrectBundle.value = true
                    }
                }
            }
        } else {
            returnedBundle = invRepo.findByHeat(heat)
        }
        isIncorrectBundle.value = returnedBundle == null
    }

    private fun getNumberOfUnidentifiedBundles(heat : String) : Int {
        return 0
    }

    private fun setIsIncorrectBl() {
        isIncorrectBl = returnedBundle!!.blNum != currentInput.value!![0].bl
    }

    private fun setIsIncorrectHeat(heat : String) {
        isIncorrectHeat = if (getType() == "load") {
            !getLoadedHeats().contains(heat) && getLoadedHeats().size == 3
        } else {
            true
        }
    }

    private fun setIsIncorrectQuantity() {
        isIncorrectQuantity = returnedBundle!!.quantity != currentInput.value!![0].bundleQuantity
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

    private fun waitForUserInputLiveData() {
        val mediatorLiveData = MediatorLiveData<Boolean>()
        mediatorLiveData.addSource(currentInput) {
            mediatorLiveData.removeSource(currentInput)
            mediatorLiveData.value = !(currentInput.value == null || currentInput.value!![0].heatNum == "")
            loading.value = mediatorLiveData.value!!
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