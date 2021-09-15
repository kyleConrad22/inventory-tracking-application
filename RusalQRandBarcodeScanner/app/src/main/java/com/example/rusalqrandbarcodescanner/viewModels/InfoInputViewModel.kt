package com.example.rusalqrandbarcodescanner.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException

class InfoInputViewModel(private val repository : UserInputRepository) : ViewModel() {
    private val currentInput = repository.currentInput.asLiveData()
    private val triggerLoader = MutableLiveData<Unit>()

    val confirmVis : LiveData<Boolean> = triggerLoader.switchMap { confirmVis() }

    val loader: MutableLiveData<String> = MutableLiveData("")
    val order: MutableLiveData<String> = MutableLiveData("")
    val load: MutableLiveData<String> = MutableLiveData("")
    val bundles: MutableLiveData<String> = MutableLiveData("")
    val bl: MutableLiveData<String> = MutableLiveData("")
    val vessel: MutableLiveData<String> = MutableLiveData("")
    val checker: MutableLiveData<String> = MutableLiveData("")
    val quantity: MutableLiveData<String> = MutableLiveData("")

    val loading = mutableStateOf(false)

    fun isLoad() : LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()
        mediatorLiveData.addSource(currentInput) { it ->
            mediatorLiveData.value =
                when {
                    it.isNullOrEmpty() -> {
                        null
                    }
                    it[0].type == "Load" -> {
                        mediatorLiveData.removeSource(currentInput)
                        true
                    }
                    it[0].type == "Reception" -> {
                        mediatorLiveData.removeSource(currentInput)
                        false
                    }
                    else -> {
                        null
                    }
                }
        }
        return mediatorLiveData
    }

    private fun getLoadVal() : String? {
        return currentInput.value!![0].type
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
                val type = getLoadVal().toString()

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
                repository.update(userInput)
            }
        }
        println(value.await())
        loading.value = false
    }

    fun refresh() {
        triggerLoader.value = Unit
    }

    private fun confirmVis() : LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()
        mediatorLiveData.addSource(isLoad()) { isLoad ->

            if (isLoad != null) {
                if (isLoad) {
                    mediatorLiveData.removeSource(isLoad())
                    mediatorLiveData.addSource(order) { ord ->
                        mediatorLiveData.value = ord != ""

                        if (ord != "") {
                            mediatorLiveData.removeSource(order)
                            mediatorLiveData.addSource(load) { loadIt ->
                                mediatorLiveData.value = loadIt != ""

                                if (loadIt != "") {
                                    mediatorLiveData.removeSource(load)
                                    mediatorLiveData.addSource(bundles) { bund ->
                                        mediatorLiveData.value = bund != ""

                                        if (bund != "") {
                                            mediatorLiveData.removeSource(bundles)
                                            mediatorLiveData.addSource(bl) { blIt ->
                                                mediatorLiveData.value = blIt != ""

                                                if (blIt != "") {
                                                    mediatorLiveData.removeSource(bl)
                                                    mediatorLiveData.addSource(quantity) { quant ->
                                                        mediatorLiveData.value = quant != ""

                                                        if (quant != "") {
                                                            mediatorLiveData.removeSource(quantity)
                                                            mediatorLiveData.addSource(loader) { loaderIt ->
                                                                mediatorLiveData.value = loaderIt != ""

                                                                if (loaderIt != "") {
                                                                    mediatorLiveData.removeSource(loader)
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    mediatorLiveData.removeSource(isLoad())
                    mediatorLiveData.addSource(vessel) { ves ->
                        mediatorLiveData.value = ves != ""

                        if (ves != "") {
                            mediatorLiveData.removeSource(vessel)
                            mediatorLiveData.addSource(checker) { check ->
                                mediatorLiveData.value = check != ""

                                if (check != "") {
                                    mediatorLiveData.removeSource(checker)
                                }
                            }
                        }
                    }
                }
            }
        }
        return mediatorLiveData
    }

    class InfoInputViewModelFactory(private val repository : UserInputRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass : Class<T>) : T {
            if (modelClass.isAssignableFrom(InfoInputViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InfoInputViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}