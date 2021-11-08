package com.example.rusalqrandbarcodescanner.util

import com.example.rusalqrandbarcodescanner.database.RusalItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")

// Sort a given list of RusalItem objects by ascending reception dateTime order
fun rusalItemListSortAscendingTime(RusalItemList : List<RusalItem>) : List<RusalItem> {
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

fun getCurrentDateTime() : String {
    return LocalDateTime.now().format(formatter)
}