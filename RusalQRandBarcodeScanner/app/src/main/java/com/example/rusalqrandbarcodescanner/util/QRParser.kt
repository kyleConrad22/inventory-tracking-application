package com.example.rusalqrandbarcodescanner.util

import com.example.rusalqrandbarcodescanner.database.RusalItem

// Handles parsing of scanned QR codes
object QRParser {

    // Parses raw value of a valid Rusal QR code and returns a RusalItem with values set through said parsing
    fun parseRusalCode(rawValue : String) : RusalItem {
        val elements = rawValue.trim('_', '|').split("_")
        return RusalItem(
            heatNum = elements[1].replace("-", ""),
            packageNum = elements[9],
            grossWeightKg = elements[5].split("/")[1],
            netWeightKg = elements[5].split("/")[0],
            quantity = elements[11],
            dimension = elements[6].replace("X", ""),
            grade = "${elements[3]} ${if (elements[10] == "BIL") "BILLETS" else "INGOTS"}",
            certificateNum = elements[4],
            blNum = "N/A",
            barcode = "${elements[0]}n"
        )
    }
}