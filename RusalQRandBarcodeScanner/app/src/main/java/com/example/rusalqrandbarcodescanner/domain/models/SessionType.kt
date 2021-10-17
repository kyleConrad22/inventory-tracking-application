package com.example.rusalqrandbarcodescanner.domain.models

enum class SessionType(val type : String) {
    SHIPMENT("Shipment"),
    RECEPTION("Reception"),
    GENERAL("General")
}