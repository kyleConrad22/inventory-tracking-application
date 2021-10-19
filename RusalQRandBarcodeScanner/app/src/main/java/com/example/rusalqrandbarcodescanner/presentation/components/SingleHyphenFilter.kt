package com.example.rusalqrandbarcodescanner.presentation.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText

fun singleHyphenFilter(text : AnnotatedString, insertionIndex : Int) : TransformedText {
    var output = ""
    for (i in text.indices) {
        if (i == insertionIndex) output += "-"
        output += text[i]
    }

    val singleHyphenOffsetTranslator = object : OffsetMapping {
        override fun originalToTransformed(offset: Int): Int {
            if (offset <= insertionIndex) return offset
            return offset + 1
        }

        override fun transformedToOriginal(offset: Int): Int {
            if (offset <= insertionIndex) return offset
            return offset - 1
        }
    }

    return TransformedText(AnnotatedString(output), singleHyphenOffsetTranslator)
}