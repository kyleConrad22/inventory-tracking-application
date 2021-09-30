package com.example.rusalqrandbarcodescanner.util.inputvalidation

import androidx.compose.runtime.MutableState

class NameValidator {

    fun isValidName(name : String) : Boolean {
        val nameClean = name.trim().replace("  ", " ")
        return !nameClean.contains(Regex("\\d")) && nameClean.split(" ").size in 2..3
    }

    fun updateName(input : String, output : MutableState<String>) {
        UpdateMutableStringState().toTitleCase(input, output)
    }
}