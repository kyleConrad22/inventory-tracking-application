package com.example.rusalqrandbarcodescanner.util

import kotlin.jvm.Throws

// Inserts insertedString into inputString at the insertionIndex, shifting current char and all following at the index to right
fun stringInsertion(inputString : String, insertionIndex : Int, insertedString : String) : String {
    return when {
        inputString.length > insertionIndex && insertionIndex > -1 -> {
            inputString.substring(0,insertionIndex) + insertedString + inputString.substring(insertionIndex)
        }
        inputString.length == insertionIndex -> {
            inputString + insertedString
        }
        else -> {
            inputString
        }
    }
}