package com.example.rusalqrandbarcodescanner.services.util

import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RusalShipmentUpdateParams(item : RusalItem? = null) {
    var heatNum : String = ""
    var workOrder : String = ""
    var loadNum : String = ""
    var loader : String = ""
    var loadTime : String = ""

    init {
        if (item != null) {
            heatNum = item.heatNum
            workOrder = item.workOrder
            loadNum = item.loadNum
            loader = item.loader
            loadTime = item.loadTime
        }
    }

    fun getUpdateParamsList(items : List<RusalItem>) : List<RusalShipmentUpdateParams> {
        val params = mutableListOf<RusalShipmentUpdateParams>()
        items.forEach { it ->
            params.add(RusalShipmentUpdateParams(it))
        }
        return params
    }
}