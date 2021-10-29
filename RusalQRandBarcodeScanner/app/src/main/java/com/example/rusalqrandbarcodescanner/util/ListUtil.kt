package com.example.rusalqrandbarcodescanner.util

import com.example.rusalqrandbarcodescanner.database.RusalItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// Sort a given list of RusalItem objects by ascending dateTime order
fun rusalItemListSortAscendingTime(RusalItemList : List<RusalItem>, formatter : DateTimeFormatter) : List<RusalItem> {
    val list = RusalItemList.toMutableList()

    if (list.size in 0..1) return list.toList()

    for (i in 1 until list.size) {
        val key = list[i]

        var j = i - 1
        while (j > -1 && LocalDateTime.parse(list[j].receptionDate, formatter) > LocalDateTime.parse(key.receptionDate, formatter)) {
            list[j + 1] = list[j--]
        }
        list[j + 1] = key
    }
    return list.toList()
}