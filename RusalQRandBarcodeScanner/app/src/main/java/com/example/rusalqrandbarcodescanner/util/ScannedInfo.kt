package com.example.rusalqrandbarcodescanner.util

import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ScannedInfo {
    var heatNum: String = ""
    var barCode: String = ""
    var packageNum: String = ""
    var netWgtKg: String = ""
    var grossWgtKg: String = ""
    var netWgtLbs: String = ""
    var grossWgtLbs: String = ""
    var scanTime: String = ""
    var blNum: String = ""
    var quantity: String = ""

    fun setValues(rawValue: String) {
        if (rawValue.contains("_")) {
            val elements = rawValue.split("_").toTypedArray()
            barCode = elements[0]
            heatNum = elements[1].replace("-", "")
            netWgtKg = elements[5].split("/")[0]
            grossWgtKg = elements[5].split("/")[1]
            netWgtLbs = elements[7].split("/")[0]
            grossWgtLbs = elements[7].split("/")[1]
            packageNum = elements[9]
            quantity = try {
                elements[11]
            } catch (e: ArrayIndexOutOfBoundsException) {
                "5"
            }
            setTime()
        }
    }

    private fun setTime(){
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM_dd_yyyy HH:mm:ss")
        val timeNow: LocalDateTime = LocalDateTime.now()
        scanTime = formatter.format(timeNow)
    }

    fun getValues(currentInventoryLineItem: CurrentInventoryLineItem){
        barCode = currentInventoryLineItem.barcode
        blNum = currentInventoryLineItem.blNum.toString()
        heatNum = currentInventoryLineItem.heatNum.toString()
        netWgtKg = currentInventoryLineItem.netWeightKg.toString()
        grossWgtKg = currentInventoryLineItem.grossWeightKg.toString()
        packageNum = currentInventoryLineItem.packageNum.toString()
        quantity = currentInventoryLineItem.quantity.toString()
        setTime()
    }

    fun setValues(
        heatNum : String,
        blNum : String,
        barcode : String = "",
        packageNum : String = "",
        netWeightKg : String = "",
        grossWeightKg : String = "",
        netWeightLbs : String = "",
        grossWeightLbs : String = "",
        quantity : String = "",
    ) {
        ScannedInfo.heatNum = heatNum
        barCode = barcode
        grossWgtKg = grossWeightKg
        netWgtKg = netWeightKg
        ScannedInfo.packageNum = packageNum
        netWgtLbs = netWeightLbs
        grossWgtLbs = grossWeightLbs
        ScannedInfo.quantity = quantity
        ScannedInfo.blNum = blNum
    }

    fun clearValues(){
        heatNum = ""
        barCode = ""
        netWgtKg = ""
        grossWgtKg = ""
        netWgtLbs = ""
        grossWgtLbs = ""
        packageNum = ""
        scanTime = ""
        blNum = ""
        quantity = ""
    }
}