package com.example.rusalqrandbarcodescanner.util

import com.example.rusalqrandbarcodescanner.database.RusalItem

fun getCommodity(item : RusalItem) : Commodity {
    if (item.grade.contains("INGOTS")) {
        return Commodity.INGOTS
    }
    return Commodity.BILLETS
}

fun isBaseHeat(heat : String) : Boolean {
    return heat.length == 6
}