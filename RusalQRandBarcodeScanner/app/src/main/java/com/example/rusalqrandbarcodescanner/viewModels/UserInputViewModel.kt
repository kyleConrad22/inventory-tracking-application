package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.*
import com.example.rusalqrandbarcodescanner.database.UserInput
import com.example.rusalqrandbarcodescanner.repositories.UserInputRepository
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class UserInputViewModel(private val repository: UserInputRepository): ViewModel() {
    val currentInput: LiveData<List<UserInput>> = repository.currentInput.asLiveData()

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun update(userInput: UserInput) = viewModelScope.launch {
        repository.update(userInput)
    }

    fun insert(userInput: UserInput) = viewModelScope.launch {
        repository.insert(userInput)
    }

    fun delete(userInput: UserInput) = viewModelScope.launch {
        repository.delete(userInput)
    }

    val loader: MutableLiveData<String> = MutableLiveData("")
    val order: MutableLiveData<String> = MutableLiveData("")
    val load: MutableLiveData<String> = MutableLiveData("")
    val bundles: MutableLiveData<String> = MutableLiveData("")
    val bl: MutableLiveData<String> = MutableLiveData("")
    val vessel: MutableLiveData<String> = MutableLiveData("")
    val checker: MutableLiveData<String> = MutableLiveData("")
    val heat: MutableLiveData<String> = MutableLiveData("")
    val quantity: MutableLiveData<String> = MutableLiveData("")

    private val triggerLoader = MutableLiveData<Unit>()

    fun refresh() {
        triggerLoader.value = Unit
    }

    val loadVis: LiveData<Boolean> = triggerLoader.switchMap{ loadLoadConfirmVis() }

    fun isLoad(): LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()
        mediatorLiveData.addSource(load) { it ->
            mediatorLiveData.value = (it != null && it != "")
        }
        return mediatorLiveData
    }

    private fun loadLoadConfirmVis(): LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()

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
        return mediatorLiveData
    }

    val receptionVis: LiveData<Boolean> = triggerLoader.switchMap { loadReceptionConfirmVis() }

    private fun loadReceptionConfirmVis(): LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()

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

        return mediatorLiveData
    }

    fun removeValues(){
        this.loader.value = ""
        this.order.value = ""
        this.load.value = ""
        this.bundles.value = ""
        this.bl.value = ""
        this.vessel.value = ""
        this.checker.value = ""
        this.heat.value = ""
        this.quantity.value = ""
    }

    class UserInputViewModelFactory(private val repository: UserInputRepository): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserInputViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserInputViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}