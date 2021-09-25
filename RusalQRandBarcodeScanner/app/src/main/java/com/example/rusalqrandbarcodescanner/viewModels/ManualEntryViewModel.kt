package com.example.rusalqrandbarcodescanner.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.util.ScannedInfo
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.CodeRepository
import com.example.rusalqrandbarcodescanner.repositories.CurrentInventoryRepository
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@DelicateCoroutinesApi
class ManualEntryViewModel(private val userRepository : UserInputRepository, private val codeRepository : CodeRepository, private val inventoryRepository : CurrentInventoryRepository) : ViewModel() {
    private val list : List<UserInput> = listOf()
    private val currentInput = mutableStateOf(list)
    private val triggerLoader = MutableLiveData<Unit>()
    private var repoData : List<CurrentInventoryLineItem> = listOf()

    val heat: MutableLiveData<String> = MutableLiveData("")
    val destination = mutableStateOf("")
    val isSearchVis = mutableStateOf(false)
    val loading = mutableStateOf(false)
    val isBaseHeat = triggerLoader.switchMap { isBaseHeatLogic() }

    private fun setTime(): String{
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM_dd_yyyy HH:mm:ss")
        val timeNow: LocalDateTime = LocalDateTime.now()
        return formatter.format(timeNow)
    }

    fun refresh() {
        triggerLoader.value = Unit
    }

    // Returns destination for NavController
    fun setDestination() {
        loading.value = true
        GlobalScope.launch(Dispatchers.Main) { // Launches coroutine in main thread
            setDestinationLogic()
            loading.value = false
        }
    }

    fun getBaseHeatList() : List<String> {
        val baseHeats = mutableListOf<String>()
        /*TODO*/
        return baseHeats
    }

    private fun isBaseHeatLogic() : LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()
        mediatorLiveData.addSource(heat) { it ->
            mediatorLiveData.removeSource(heat)
            if (it.length in 6..9) {
                mediatorLiveData.value = it.length == 6
                isSearchVis.value = true
            } else {
                mediatorLiveData.value = false
                isSearchVis.value = false
            }
        }
        return mediatorLiveData
    }

    // Makes synchronous call for database query of returning line items by their base heat
    private suspend fun findByBaseHeat(heat: String): List<CurrentInventoryLineItem>? {
        var result : List<CurrentInventoryLineItem>? = null
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                result = inventoryRepository.findByBaseHeat("%$heat%")
            }
        }
        println(value.await())
        return result
    }

    private suspend fun update(userInput : UserInput) {
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                userRepository.update(userInput)
            }
        }
        println(value.await())
    }

    private suspend fun getInput() {
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                currentInput.value = userRepository.getInputSuspend()!!
            }
        }
        println(value.await())
    }

    private suspend fun saveHeatInput() {
        var userInput : UserInput? = null
        getInput()
        if (!currentInput.value.isNullOrEmpty()) {
            val input = currentInput.value[0]
            userInput = UserInput(
                id = "data",
                load = input.load,
                order = input.order,
                loader = input.loader,
                checker = input.checker,
                vessel = input.vessel,
                bl = input.bl,
                bundleQuantity = input.bundleQuantity,
                heatNum = heat.value,
                pieceCount = input.pieceCount,
                type = input.type
            )
        }
        update(userInput!!)
    }

    // Synchronous call to repository to insert line item
    suspend fun insert(lineItem: CurrentInventoryLineItem) {
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                inventoryRepository.insert(lineItem)
            }
        }
        println(value.await())
    }

    // Creates and adds new CurrentInventoryLineItem to be added to the database ScannedInfo
    private suspend fun addNewItemByBaseHeat() {
        var result : CurrentInventoryLineItem? = null
        val listByHeat = findByBaseHeat(heat.value!!)
        println("HERE")
        if (!listByHeat.isNullOrEmpty()) {
            println("AND HERE")
            val listByBarcode = findByBarcodes("${heat.value}u")
            val barcode = if (!listByBarcode.isNullOrEmpty()) { "${heat.value}u1" } else { "${heat.value}u${listByBarcode!!.size + 1}" }

            result = CurrentInventoryLineItem(
                heatNum = heat.value!!,
                packageNum = "N/A",
                grossWeightKg = "N/A",
                netWeightKg = "N/A",
                quantity = listByHeat[0].quantity,
                dimension = listByHeat[0].dimension,
                grade = listByHeat[0].grade,
                certificateNum = listByHeat[0].certificateNum,
                blNum = listByHeat[0].blNum,
                barcode = barcode,
                workOrder = currentInput.value[0].order,
                loadNum = currentInput.value[0].load,
                loader = currentInput.value[0].loader,
                loadTime = setTime()
            )
        }
        ScannedInfo.getValues(result!!)
        insert(result)
    }

    private suspend fun destinationLogic() {
        val repoData = findByBaseHeat(heat.value!!) {

        }
    }

    // Encapsulates methods to get destination for synchronous calls
    private suspend fun setDestinationLogic() {
        val repoData = findByBaseHeat(heat.value!!)

        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                val blList = getBlList(repoData)
                val quantityList = getQuantityList(repoData)

                destination.value =
                    when {
                        blList.isNullOrEmpty() || quantityList.isNullOrEmpty() -> "N/A"
                        blList.size == 1 && quantityList.size == 1 -> Screen.ScannedInfoScreen.title
                        blList.size > 1 && quantityList.size == 1 -> Screen.BlOptionsScreen.title
                        blList.size == 1 && quantityList.size > 1 -> Screen.QuantityOptionsScreen.title
                        blList.size > 1 && quantityList.size > 1 -> Screen.ToBeImplementedScreen.title
                        else -> "N/A"
                    }
                if (destination.value != "N/A"){
                    saveHeatInput()
                }
                if (destination.value == Screen.ScannedInfoScreen.title) {
                    addNewItemByBaseHeat()
                }
            }
        }
        println(value.await())
    }

    // Returns list of all BLs found for given heat number, by base heat only
    private fun getBlList(repoData : List<CurrentInventoryLineItem>?) : List<String> {
        val blList = mutableListOf<String>()
        if (!repoData.isNullOrEmpty()) {
            for (lineItem in repoData) {
                if (blList.find { it == lineItem.blNum } == null) {
                    Log.d("DEBUG", lineItem.blNum.toString())
                    blList.add(lineItem.blNum!!)
                }
            }
        }
        return blList
    }

    // Returns list of all quantities found for given heat number, by base heat only
    private fun getQuantityList(repoData : List<CurrentInventoryLineItem>?) : List<String> {
        val quantityList = mutableListOf<String>()
        if (!repoData.isNullOrEmpty()) {
            for (lineItem in repoData) {
                if (quantityList.find { it == lineItem.quantity } == null) {
                    Log.d("DEBUG", lineItem.quantity.toString())
                    quantityList.add(lineItem.quantity!!)
                }
            }
        }
        return quantityList
    }

    private suspend fun findByBarcodes(barcode: String) : List<CurrentInventoryLineItem>? {
        var result : List<CurrentInventoryLineItem>? = null
        val value = GlobalScope.async {
            withContext(Dispatchers.Main) {
                result = inventoryRepository.findByBarcodes("%$barcode%")
            }
        }
        println(value.await())
        return result
    }

    class ManualEntryViewModelFactory(private val userRepository : UserInputRepository, private val codeRepository : CodeRepository, private val inventoryRepository : CurrentInventoryRepository) : ViewModelProvider.Factory {
        override fun<T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(ManualEntryViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ManualEntryViewModel(userRepository, codeRepository, inventoryRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}