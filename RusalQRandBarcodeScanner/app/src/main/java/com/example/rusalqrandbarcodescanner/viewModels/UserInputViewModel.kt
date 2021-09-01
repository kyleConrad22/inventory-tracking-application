package com.example.rusalqrandbarcodescanner.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
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

    fun update(
        loader: String,
        order: String,
        load: String,
        bundles: String,
        bl: String,
        vessel: String,
        checker: String,
        heat: String,
        quantity: String
    ) {
        this.loader.value = loader
        this.order.value = order
        this.load.value = load
        this.bundles.value = bundles
        this.bl.value = bl
        this.vessel.value = vessel
        this.checker.value = checker
        this.heat.value = heat
        this.quantity.value = quantity
    }

    fun updateHeat(heat: String){
        this.heat.value = heat
    }

    fun 
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