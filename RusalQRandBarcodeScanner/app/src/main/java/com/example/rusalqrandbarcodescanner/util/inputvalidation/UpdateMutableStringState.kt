package com.example.rusalqrandbarcodescanner.util.inputvalidation

import androidx.compose.runtime.MutableState

class UpdateMutableStringState() {

    fun insertCharIntoString(input : String, output : MutableState<String>, index : Int, char : Char) {
        if (input.length == index + 1 && input.length > output.value.length) {
            output.value = input.substring(0, index) + char + input[index]
        } else if (input.length == index + 1 && input < output.value) {
            output.value = input.substring(0, index)
        } else {
            output.value = input
        }
    }

    fun capitalizeInput(input : String, output : MutableState<String>) {
        output.value = input.uppercase()
    }

    fun toTitleCase(input : String, output : MutableState<String>) {
        if (input.length == 1) {
            output.value = input.uppercase()
        } else if (output.value[output.value.length - 1] == ' ' || input.length > output.value.length) {
            output.value = input
        }
    }

}