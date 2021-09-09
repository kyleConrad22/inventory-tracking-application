package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.*
import java.lang.IllegalArgumentException

class UserInputViewModel(): ViewModel() {
    var loader: MutableLiveData<String> = MutableLiveData("")
    var order: MutableLiveData<String> = MutableLiveData("")
    var load: MutableLiveData<String> = MutableLiveData("")
    var bundles: MutableLiveData<String> = MutableLiveData("")
    var bl: MutableLiveData<String> = MutableLiveData("")
    var vessel: MutableLiveData<String> = MutableLiveData("")
    var checker: MutableLiveData<String> = MutableLiveData("")
    var heat: MutableLiveData<String> = MutableLiveData("")
    var quantity: MutableLiveData<String> = MutableLiveData("")

    fun updateHeat(heat: String){
        this.heat.value = heat
    }

    fun loadConfirmIsVisible(): LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()

        mediatorLiveData.addSource(order) { ord ->
            mediatorLiveData.removeSource(order)
            mediatorLiveData.value = ord != ""

            if (mediatorLiveData.value == true) {
                mediatorLiveData.addSource(load) { loadIt ->
                    mediatorLiveData.removeSource(load)
                    mediatorLiveData.value = loadIt != ""

                    if (mediatorLiveData.value == true) {
                        mediatorLiveData.addSource(bundles) { bund ->
                            mediatorLiveData.removeSource(bundles)
                            mediatorLiveData.value = bund != ""

                            if (mediatorLiveData.value == true) {
                                mediatorLiveData.addSource(bl) { blIt ->
                                    mediatorLiveData.removeSource(bl)
                                    mediatorLiveData.value = blIt != ""

                                    if (mediatorLiveData.value == true) {
                                        mediatorLiveData.addSource(quantity) { quant ->
                                            mediatorLiveData.removeSource(quantity)
                                            mediatorLiveData.value = quant != ""

                                            if (mediatorLiveData.value == true) {
                                                mediatorLiveData.addSource(loader) { loaderIt ->
                                                    mediatorLiveData.removeSource(loader)
                                                    mediatorLiveData.value = loaderIt != ""
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

    fun receptionConfirmIsVisible(): LiveData<Boolean> {
        val mediatorLiveData = MediatorLiveData<Boolean>()

        mediatorLiveData.addSource(vessel) { ves ->
            mediatorLiveData.removeSource(vessel)
            mediatorLiveData.value = ves != ""

            if (mediatorLiveData.value == true) {
                mediatorLiveData.addSource(checker) { check ->
                    mediatorLiveData.removeSource(checker)
                    mediatorLiveData.value = check != ""
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