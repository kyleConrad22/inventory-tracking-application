package com.example.rusalqrandbarcodescanner.services

import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class RusalReceptionUpdateParams(item : RusalItem? = null) {
    var heatNum : String
    var checker : String
    var receptionDate : String

    init {
        if (item != null) {
            heatNum = item.heatNum
            checker = item.checker
            receptionDate = item.receptionDate

        } else {
            heatNum = ""
            checker = ""
            receptionDate = ""
        }
    }

    fun getUpdateParamsList(items : List<RusalItem>) : List<RusalReceptionUpdateParams> {
        val params = mutableListOf<RusalReceptionUpdateParams>()
        items.forEach { it ->
            params.add(RusalReceptionUpdateParams(it))
        }
        return params
    }
}