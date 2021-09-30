package com.example.rusalqrandbarcodescanner.util_test.inputvalidation_test

import org.junit.Test

abstract class FromListValidatorTest<T> {

    @Test
    abstract fun isValidItemTestInList()

    @Test
    abstract fun isValidItemTestNotInList()

    @Test
    abstract fun isValidItemTestEmptyList()
}