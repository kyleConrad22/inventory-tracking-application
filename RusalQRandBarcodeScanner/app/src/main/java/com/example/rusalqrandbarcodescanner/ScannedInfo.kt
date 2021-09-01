package com.example.rusalqrandbarcodescanner

import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.coroutines.coroutineContext

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

    fun setValues(rawValue: String) {
        val elements = rawValue.split("_").toTypedArray()
        qrCode = rawValue
        barCode = elements[0]
        heatNum = elements[1]
        netWgtKg = elements[5].split("/")[0]
        grossWgtKg = elements[5].split("/")[1]
        netWgtLbs = elements[7].split("/")[0]
        grossWgtLbs = elements[7].split("/")[1]
        packageNum = elements[9]
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
        setTime()
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
            loader = loader
        )
        viewModel.order.removeObserver(orderObserver)
        viewModel.load.removeObserver(loadNumObserver)
        viewModel.loader.removeObserver(loaderObserver)
        return result
    }
}