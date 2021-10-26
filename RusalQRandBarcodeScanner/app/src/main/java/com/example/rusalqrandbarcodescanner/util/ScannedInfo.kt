package com.example.rusalqrandbarcodescanner.util

/* Acts as a relay between UI and ViewModels - takes raw value from scanned info passes to  */

object ScannedInfo {
    var rawValue : String = ""
    var heatNum: String = ""
    var isScanned : Boolean = false

    fun setValues(rawValue: String) {
        if (rawValue.contains("_")) {
            val elements = rawValue.split("_").toTypedArray()
            heatNum = elements[1]
        }
    }

}