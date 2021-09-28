package com.example.rusalqrandbarcodescanner.domain.models

import androidx.compose.ui.text.toLowerCase
import com.example.rusalqrandbarcodescanner.presentation.components.autocomplete.AutoCompleteEntity
import java.util.*

data class Bl(val blNumber : String) : AutoCompleteEntity {
    override fun filter(query: String): Boolean {
        return blNumber.uppercase(Locale.getDefault())
            .startsWith(query.uppercase(Locale.getDefault()))
    }
}