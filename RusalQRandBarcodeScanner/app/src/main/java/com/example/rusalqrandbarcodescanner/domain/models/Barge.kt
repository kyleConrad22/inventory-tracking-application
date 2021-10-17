package com.example.rusalqrandbarcodescanner.domain.models

import com.example.rusalqrandbarcodescanner.presentation.components.autocomplete.AutoCompleteEntity
import java.util.*

data class Barge(val text : String) : AutoCompleteEntity {
    override fun filter(query : String) : Boolean {
        return text.uppercase(Locale.getDefault())
            .startsWith(query.uppercase(Locale.getDefault()))
    }
}
