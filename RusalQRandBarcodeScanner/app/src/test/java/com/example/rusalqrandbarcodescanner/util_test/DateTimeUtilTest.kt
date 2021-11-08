package com.example.rusalqrandbarcodescanner.util_test

import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.util.getCurrentDateTime
import com.example.rusalqrandbarcodescanner.util.rusalItemListSortAscendingTime
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DateTimeUtilTest {

    @Nested
    inner class RusalItemSortAscendingTime {

        private lateinit var itemList : List<RusalItem>

        @Test
        fun `returns empty list given empty list`() {
            itemList = listOf()
            assertEquals(itemList, rusalItemListSortAscendingTime(itemList))
        }

        @Test
        fun `returns list with single element given list with single element`() {
            itemList = listOf(RusalItem(barcode = "Test", receptionDate = getCurrentDateTime()))
            assertEquals(itemList, rusalItemListSortAscendingTime(itemList))
        }

        @Test
        fun `returns list sorted by ascending dateTime when given list with multiple elements`() {
            val item1 = RusalItem(barcode = "Test1", receptionDate = "02/01/1999 05:05:05")
            val item2 = RusalItem(barcode = "Test2", receptionDate = "01/01/1999 05:00:00")
            val item3 = RusalItem(barcode = "Test3", receptionDate = "01/15/1999 05:00:00")
            itemList = listOf(item1, item2, item3)
            assertEquals(
                listOf(item2, item3, item1),
                rusalItemListSortAscendingTime(itemList)
            )
        }
    }
}