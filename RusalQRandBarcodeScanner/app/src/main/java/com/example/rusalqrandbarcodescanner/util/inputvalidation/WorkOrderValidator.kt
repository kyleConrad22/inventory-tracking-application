package com.example.rusalqrandbarcodescanner.util.inputvalidation

import androidx.compose.runtime.MutableState

class WorkOrderValidator {

    fun isValidWorkOrder(workOrder : String) : Boolean {
        return workOrder.contains(Regex("RAC-\\d{6}"))
    }

    fun updateWorkOrder(input : String, output : MutableState<String>) {
        UpdateMutableStringState().insertCharIntoString(input, output, 3, '-')
        UpdateMutableStringState().capitalizeInput(output.value, output)
    }
}