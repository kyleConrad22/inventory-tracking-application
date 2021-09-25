package com.example.rusalqrandbarcodescanner.util

import androidx.lifecycle.Observer
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ScannedInfo {
    var qrCode: String = ""
    var heatNum: String = ""
    var barCode: String = ""
    var packageNum: String = ""
    var netWgtKg: String = ""
    var grossWgtKg: String = ""
    var netWgtLbs: String = ""
    var grossWgtLbs: String = ""
    var scanTime: String = ""
    var blNum: String = ""
    var workOrder: String = ""
    var loadNum: String = ""
    var loader: String = ""
    var quantity: String = ""

    fun setValues(rawValue: String) {
        val elements = rawValue.split("_").toTypedArray()
        qrCode = rawValue
        barCode = elements[0]
        heatNum = elements[1].replace("-","")
        netWgtKg = elements[5].split("/")[0]
        grossWgtKg = elements[5].split("/")[1]
        netWgtLbs = elements[7].split("/")[0]
        grossWgtLbs = elements[7].split("/")[1]
        packageNum = elements[9]
        try {
            quantity = elements[11]
        } catch (e: ArrayIndexOutOfBoundsException) {
            quantity = "5"
        }
        setTime()
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
        qrCode = ""
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

    fun toScannedCode(viewModel: UserInputViewModel): ScannedCode{
        val orderObserver = Observer<String> { it ->
            workOrder = it
        }
        val loadNumObserver = Observer<String> { it ->
            loadNum = it
        }
        val loaderObserver = Observer<String> { it ->
            loader = it
        }

        viewModel.order.observeForever(orderObserver)
        viewModel.load.observeForever(loadNumObserver)
        viewModel.loader.observeForever(loaderObserver)
        val result = ScannedCode(
            barCode = barCode,
            heatNum = heatNum,
            netWgtKg = netWgtKg,
            grossWgtKg = grossWgtKg,
            netWgtLbs = netWgtLbs,
            grossWgtLbs = grossWgtLbs,
            packageNum = packageNum,
            scanTime = scanTime,
            workOrder = workOrder,
            loadNum = loadNum,
            loader = loader,
            bl = blNum,
            quantity = quantity
        )
        viewModel.order.removeObserver(orderObserver)
        viewModel.load.removeObserver(loadNumObserver)
        viewModel.loader.removeObserver(loaderObserver)
        return result
    }
}