package com.example.rusalqrandbarcodescanner.util.inputvalidation

import androidx.compose.runtime.MutableState

class NameValidator {

    fun isValidName(name : String) : Boolean {
        val nameClean = name.trim().replace(Regex("\\s+"), " ")
        return !nameClean.replace(" ", "").contains(Regex("[^a-zA-Z]")) && nameClean.split(" ").size in 2..3
    }

    fun updateName(input : String, output : MutableState<String>) {
        UpdateMutableStringState().capitalizeInput(input, output)
    }
}