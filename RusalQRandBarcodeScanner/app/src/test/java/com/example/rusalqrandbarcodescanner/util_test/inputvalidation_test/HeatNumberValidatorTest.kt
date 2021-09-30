package com.example.rusalqrandbarcodescanner.util_test.inputvalidation_test

import com.example.rusalqrandbarcodescanner.util.inputvalidation.HeatNumberValidator
import org.junit.Assert
import org.junit.Test

class HeatNumberValidatorTest {

    @Test
    fun isValidHeatTest1() {
        Assert.assertTrue(HeatNumberValidator().isValidHeat("100000"))
    }

    @Test
    fun isValidHeatTest2() {
        Assert.assertTrue(HeatNumberValidator().isValidHeat("29587323"))
    }

    @Test
    fun isValidHeatTest3() {
        Assert.assertTrue(HeatNumberValidator().isValidHeat("295873-23"))
    }

    @Test
    fun isValidHeatTest4() {
        Assert.assertTrue(HeatNumberValidator().isValidHeat("295873-232"))

    }

    @Test
    fun isValidHeatTest5() {
        Assert.assertTrue(HeatNumberValidator().isValidHeat("295873223"))
    }

    @Test
    fun isValidHeatTest6() {
        Assert.assertTrue(HeatNumberValidator().isValidHeat("295873-"))
    }

    @Test
    fun isValidHeatTest7() {
        Assert.assertFalse(HeatNumberValidator().isValidHeat(""))
    }

    @Test
    fun isValidHeatTest8() {
        Assert.assertFalse(HeatNumberValidator().isValidHeat("99999"))
    }

    @Test
    fun isValidHeatTest9() {
        Assert.assertFalse(HeatNumberValidator().isValidHeat("1000000000"))
    }

    @Test
    fun isValidHeatTest10() {
        Assert.assertFalse(HeatNumberValidator().isValidHeat("20fh2"))
    }

    @Test
    fun isValidHeatTest11() {
        Assert.assertFalse(HeatNumberValidator().isValidHeat("295873-!"))
    }

    @Test
    fun isValidHeatTest12() {
        Assert.assertFalse(HeatNumberValidator().isValidHeat("295873 12"))
    }
}