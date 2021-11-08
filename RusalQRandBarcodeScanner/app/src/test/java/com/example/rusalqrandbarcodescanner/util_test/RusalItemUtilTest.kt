package com.example.rusalqrandbarcodescanner.util_test

import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.util.Commodity
import com.example.rusalqrandbarcodescanner.util.getCommodity
import com.example.rusalqrandbarcodescanner.util.isBaseHeat
import org.junit.Assert.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RusalItemUtilTest {

    @Nested
    inner class GetCommodityTest {

        @Test
        fun `returns INGOTS given ingots is in input item grade`() {
            assertEquals(Commodity.INGOTS, getCommodity(RusalItem(barcode = "Test", grade = "INGOTS")))
        }
        
        @Test
        fun `returns BILLETS given ingots is not in input item grade`() {
            assertEquals(Commodity.BILLETS, getCommodity(RusalItem(barcode = "Test", grade = "Test")))
        }
    }

    @Nested
    inner class IsBaseHeatTest {

        @Test
        fun `return false when input length is greater than 6`() {
            assertFalse(isBaseHeat("1234567"))
        }

        @Test
        fun `return false when input length is less than 6`() {
            assertFalse(isBaseHeat("12345"))
        }

        @Test
        fun `return true when input length is equal to 6 and is an integer`() {
            assertTrue(isBaseHeat("123456"))
        }

        @Test
        fun `return false when input is not an integer`() {

            for (i in (32..47).union(58..126)) {
                assertFalse(isBaseHeat("12345${i.toChar()}"))
            }
        }
    }
}