package com.example.rusalqrandbarcodescanner.util.inputvalidation

object QRValidator {

    // Returns true if code passed via parameter is valid rusal QR code, false otherwise
    fun isValidRusalCode(rawValue : String) : Boolean {
        if (rawValue.contains("_") && rawValue.last() == '|') {
            val rawValueCleanSplit = rawValue.trim('|', '_').split("_")
            if (rawValueCleanSplit.size == 12) {
                if (rawValueCleanSplit[1].contains("-")) {
                    return true
                }
            }
        }
        return false
    }
}