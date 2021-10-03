package com.example.rusalqrandbarcodescanner.util_test.inputvalidation_test

import com.example.rusalqrandbarcodescanner.util.inputvalidation.BasicItemValidator
import org.junit.Assert
import org.junit.Test

class BasicItemValidatorTest : FromListValidatorTest<String>() {

    @Test
    override fun isValidItemTestInList() {
        Assert.assertTrue(BasicItemValidator().isValidItem("Something", listOf("Something", "Nothing", "Something Else")))
    }

    @Test
    override fun isValidItemTestNotInList() {
        Assert.assertFalse(BasicItemValidator().isValidItem("Something", listOf("Nothing", "Something Else")))
    }

    @Test
    override fun isValidItemTestEmptyList() {
        Assert.assertFalse(BasicItemValidator().isValidItem("Something", listOf()))
    }

}