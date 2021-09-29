package com.example.rusalqrandbarcodescanner.util

object ScannedInfo {
    var heatNum: String = ""
    var isScanned : Boolean = false

    fun setValues(rawValue: String) {
        if (rawValue.contains("_")) {
            val elements = rawValue.split("_").toTypedArray()
            heatNum = elements[1].replace("-", "")
        }
    }

}