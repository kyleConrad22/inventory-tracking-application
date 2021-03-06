package com.example.rusalqrandbarcodescanner.util.inputvalidation

import androidx.compose.runtime.MutableState

class WorkOrderValidator {

    fun isValidWorkOrder(workOrder : String) : Boolean {
        return workOrder.matches(Regex("RAC-\\d{6}"))
    }

    fun updateWorkOrder(input : String, output : MutableState<String>) {
        UpdateMutableStringState().capitalizeInput(input, output)
    }
}