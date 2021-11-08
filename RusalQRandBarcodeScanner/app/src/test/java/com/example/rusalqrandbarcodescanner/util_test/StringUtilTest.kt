package com.example.rusalqrandbarcodescanner.util_test

import com.example.rusalqrandbarcodescanner.util.stringInsertion
import org.junit.Assert.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class StringUtilTest {

    @Nested
    inner class StringInsertionText {

        private val inputString = "Test"
        private val insertionString = "Best"

        @Test
        fun `returns the given input string if the insertion index is greater than the input string length`() {
            assertEquals(inputString, stringInsertion(inputString, 5, insertionString))
        }

        @Test
        fun `returns the given input string if the insertion index is less negative`() {
            assertEquals(inputString, stringInsertion(inputString, -1, insertionString))
        }

        @Test
        fun `returns the given input string with the insertionString inserted at specified index`() {
            assertEquals(inputString + insertionString, stringInsertion(inputString, 4, insertionString))
            assertEquals(insertionString + inputString, stringInsertion(inputString, 0, insertionString))
            assertEquals("TeBestst", stringInsertion(inputString, 2, insertionString))
        }
    }
}