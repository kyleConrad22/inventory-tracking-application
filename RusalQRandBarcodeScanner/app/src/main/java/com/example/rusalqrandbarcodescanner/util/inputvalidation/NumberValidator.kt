package com.example.rusalqrandbarcodescanner.util.inputvalidation

import androidx.compose.runtime.MutableState

class NumberValidator {

    fun isValidNumber(number : String) : Boolean {
        return !number.contains(Regex("\\D")) && number.isNotEmpty()
    }

    fun updateNumber(input : String, output : MutableState<String>) {
        output.value = input
    }
}