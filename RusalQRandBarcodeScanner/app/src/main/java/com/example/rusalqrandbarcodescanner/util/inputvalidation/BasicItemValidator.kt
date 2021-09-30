package com.example.rusalqrandbarcodescanner.util.inputvalidation

import androidx.compose.runtime.MutableState

class BasicItemValidator : FromListValidator<String>{

    fun updateItem(input : String, output : MutableState<String>) {
        UpdateMutableStringState().capitalizeInput(input, output)
    }

}