package com.example.rusalqrandbarcodescanner.util_test.inputvalidation_test

import com.example.rusalqrandbarcodescanner.util.inputvalidation.NumberValidator
import org.junit.Assert
import org.junit.Test

class NumberValidatorTest {

    @Test
    fun isValidNumberTest1() {
        Assert.assertTrue(NumberValidator().isValidNumber("5"))
    }

    @Test
    fun isValidNumberTest2() {
        Assert.assertFalse(NumberValidator().isValidNumber("-1"))
    }

    @Test
    fun isValidNumberTest3() {
        Assert.assertFalse(NumberValidator().isValidNumber(""))
    }

    @Test
    fun isValidNumberTest4() {
        Assert.assertFalse(NumberValidator().isValidNumber("35 "))
    }

    @Test
    fun isValidNumberTest5() {
        Assert.assertFalse(NumberValidator().isValidNumber("uiwiufhwpfh0"))
    }

}