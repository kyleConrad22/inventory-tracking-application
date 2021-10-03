package com.example.rusalqrandbarcodescanner.util_test.inputvalidation_test

import com.example.rusalqrandbarcodescanner.util.inputvalidation.NameValidator
import org.junit.Assert
import org.junit.Test

class NameValidatorTest {

    @Test
    fun isValidNameTest1() {
        Assert.assertTrue(NameValidator().isValidName("osigh ohpfih iefe"))
    }

    @Test
    fun isValidNameTest2() {
        Assert.assertTrue(NameValidator().isValidName("aifhe fowih"))
    }

    @Test
    fun isValidNameTest3() {
        Assert.assertFalse(NameValidator().isValidName("fowh-ofh_fw woif"))
    }

    @Test
    fun isValidNameTest4() {
        Assert.assertFalse(NameValidator().isValidName(""))
    }

    @Test
    fun isValidNameTest5() {
        Assert.assertFalse(NameValidator().isValidName("9hfs 09jl"))
    }

    @Test
    fun isValidNameTest6() {
        Assert.assertFalse(NameValidator().isValidName("owhfow"))
    }

    @Test
    fun isValidNameTest7() {
        Assert.assertTrue(NameValidator().isValidName("Kelp  Forest"))
    }

    @Test
    fun isValidNameTest8() {
        Assert.assertTrue(NameValidator().isValidName("   Deep Barnacle "))
    }

    @Test
    fun isValidNameTest9() {
        Assert.assertTrue(NameValidator().isValidName("Oof   Beesaly"))
    }

    @Test
    fun isValidNameTest10() {
        Assert.assertTrue(NameValidator().isValidName("Kyle    Conrad"))
    }

    @Test
    fun isValidNameTest11() {
        Assert.assertTrue(NameValidator().isValidName("Kyle  Brandon      Conrad"))
    }

    @Test
    fun isValidNameTest12() {
        Assert.assertFalse(NameValidator().isValidName("osigh ohpfih iefe fpjf"))
    }
}