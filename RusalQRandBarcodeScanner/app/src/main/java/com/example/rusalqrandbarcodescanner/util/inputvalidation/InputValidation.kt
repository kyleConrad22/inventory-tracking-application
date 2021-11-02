package com.example.rusalqrandbarcodescanner.util.inputvalidation

import com.example.rusalqrandbarcodescanner.domain.models.Barge
import com.example.rusalqrandbarcodescanner.domain.models.Bl

class InputValidation {

    companion object {

        // Tests to see if the input string has a length of less than or equal to the requested length
        fun lengthValidation(input: String, length: Int): Boolean {
            return input.length <= length
        }

        // Returns true if order is formatted correctly, false otherwise
        fun validateOrder(order : String) : Boolean {
            return order.startsWith("RAC", true) && order.length == 9
        }

        // Returns true if input is castable to an integer or empty, false otherwise
        fun integerValidation(input : String) : Boolean {
            if (input.isEmpty()) return true
            return try {
                Integer.parseInt(input)
                true
            } catch (e : Exception) {
                false
            }
        }

        // Returns true if barge list contains the given input, false otherwise
        fun inBargeListValidation(input : String, bargeList : List<Barge>) : Boolean {
            if (input == "") return true
            bargeList.forEach { barge ->
                if (barge.text == input) return true
            }
            return false
        }

        //Returns true if bl list contains the given input, false otherwise
        fun inBlListValidation(input : String, blList : List<Bl>) : Boolean {
            if (input == "") return true
            blList.forEach { bl ->
                if (bl.text == input) return true
            }
            return false
        }
    }
}