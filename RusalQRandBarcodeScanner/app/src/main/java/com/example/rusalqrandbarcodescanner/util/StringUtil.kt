package com.example.rusalqrandbarcodescanner.util

fun displayedStringPostStringInsertion(string : String, insertionIndex : Int, insertedString : String) : String {
    return if (string.length > insertionIndex) {
        string.substring(0,insertionIndex) + insertedString + string.substring(insertionIndex)
    } else {
        string
    }
}