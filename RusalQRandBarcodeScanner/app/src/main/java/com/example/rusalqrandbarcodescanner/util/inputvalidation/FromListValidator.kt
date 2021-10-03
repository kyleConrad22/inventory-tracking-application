package com.example.rusalqrandbarcodescanner.util.inputvalidation

interface FromListValidator<T> {

    fun isValidItem(item : T, validItems : List<T>) : Boolean {
        return item in validItems
    }

}