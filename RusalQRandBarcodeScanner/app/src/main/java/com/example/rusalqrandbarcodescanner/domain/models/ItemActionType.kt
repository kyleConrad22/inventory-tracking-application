package com.example.rusalqrandbarcodescanner.domain.models

enum class ItemActionType(val type : String) {
    DUPLICATE("Duplicate"),
    INCORRECT_BL("Incorrect Bl"),
    INCORRECT_PIECE_COUNT("Incorrect Quantity"),
    INVALID_HEAT("Invalid Heat"),
    NOT_IN_LOADED_HEATS("Not In Loaded Heats"),
    MULTIPLE_BLS_OR_PIECE_COUNTS("Multiple BLs or Quantities"),
    INCORRECT_BARGE("Incorrect Barge"),
    VALID("Can Be Added")
}