package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.*
import java.lang.IllegalArgumentException

class UserInputViewModel(): ViewModel() {
    val loader: MutableLiveData<String> = MutableLiveData("")
    val order: MutableLiveData<String> = MutableLiveData("")
    val load: MutableLiveData<String> = MutableLiveData("")
    val bundles: MutableLiveData<String> = MutableLiveData("")
    val bl: MutableLiveData<String> = MutableLiveData("")
    val vessel: MutableLiveData<String> = MutableLiveData("")
    val checker: MutableLiveData<String> = MutableLiveData("")
    val heat: MutableLiveData<String> = MutableLiveData("")
    val quantity: MutableLiveData<String> = MutableLiveData("")

    fun updateHeat(heat: String){
        this.heat.value = heat
    }

    private val triggerLoader = MutableLiveData<Unit>()

    fun refresh() {
        triggerLoader.value = Unit
    }

    val loadVis: LiveData<Boolean> = triggerLoader.switchMap{ loadLoadConfirmVis() }

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

    class UserInputViewModelFactory(): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UserInputViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UserInputViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}