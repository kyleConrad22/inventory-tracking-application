/*
    Example of usage of FilterExtension to filter for items of a List<String> type
*/

package com.example.rusalqrandbarcodescanner.presentation.components.autocomplete

import java.util.*

val items = listOf(
    "Paulo Pereita",
    "Daenerys Targaryen",
    "Jon Snow",
    "Sansa Stark",
)
val autoCompleteEntities = items.asAutoCompleteEntities(
    filter = { item, query ->
        item.lowercase(Locale.getDefault())
            .startsWith(query.lowercase(Locale.getDefault()))
    }
)