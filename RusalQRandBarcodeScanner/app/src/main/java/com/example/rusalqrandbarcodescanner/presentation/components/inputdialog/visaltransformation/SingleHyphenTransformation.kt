package com.example.rusalqrandbarcodescanner.presentation.components.inputdialog.visaltransformation

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class SingleHyphenTransformation(private val insertionIndex : Int) : VisualTransformation {

    override fun filter(text: AnnotatedString): TransformedText {
        return singleHyphenFilter(text = text, insertionIndex = insertionIndex)
    }
}