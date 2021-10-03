package com.example.rusalqrandbarcodescanner.util_test.inputvalidation_test

import com.example.rusalqrandbarcodescanner.util.inputvalidation.WorkOrderValidator
import org.junit.Test
import org.junit.Assert;

class WorkOrderValidatorTest {

    @Test
    fun isValidWorkOrderTest1() {
        Assert.assertTrue(WorkOrderValidator().isValidWorkOrder("RAC-012222"))
    }

    @Test
    fun isValidWorkOrderTest2() {
        Assert.assertFalse(WorkOrderValidator().isValidWorkOrder("rac-999024"))
    }

    @Test
    fun isValidWorkOrderTest3() {
        Assert.assertFalse(WorkOrderValidator().isValidWorkOrder(""))
    }

    @Test
    fun isValidWorkOrderTest4() {
        Assert.assertFalse(WorkOrderValidator().isValidWorkOrder("foiogw3809"))
    }

    @Test
    fun isValidWorkOrderTest5() {
        Assert.assertFalse(WorkOrderValidator().isValidWorkOrder("RAC-1252343"))
    }

    @Test
    fun isValidWorkOrderTest6() {
        Assert.assertFalse(WorkOrderValidator().isValidWorkOrder("RAC888534"))
    }

    @Test
    fun isValidWorkOrderTest7() {
        Assert.assertFalse(WorkOrderValidator().isValidWorkOrder("RAC-"))
    }

    @Test
    fun isValidWorkOrderTest8() {
        Assert.assertFalse(WorkOrderValidator().isValidWorkOrder("RAC-03666"))
    }

    @Test
    fun isValidWorkOrderTest9() {
        Assert.assertFalse(WorkOrderValidator().isValidWorkOrder("TAD-000000"))
    }

    @Test
    fun isValidWorkOrderTest10() {
        Assert.assertFalse(WorkOrderValidator().isValidWorkOrder("fRAC-042222F"))
    }
}