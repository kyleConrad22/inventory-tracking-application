package com.example.rusalqrandbarcodescanner.util.inputvalidation

import androidx.compose.runtime.MutableState

class HeatNumberValidator {

    fun isValidHeat(heat : String) : Boolean {
        return try {
            heat.replace("-", "").toInt() in 100000..999999999
        } catch (e: NumberFormatException) {
            false
        }
    }

    fun updateHeat(input : String, output : MutableState<String>) {
        UpdateMutableStringState().insertCharIntoString(input, output, 6, '-')
    }
}