package com.example.rusalqrandbarcodescanner.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun SingleHyphenTransformedInputDialog(
    label : String,
    userInput : MutableState<String>,
    refresh : (input : String)-> Unit,
    focusManager : FocusManager,
    lastInput : Boolean,
    keyBoardType : KeyboardType,
    insertionIndex : Int
    ) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(.9f),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyBoardType, imeAction = if (lastInput) ImeAction.Done else ImeAction.Next),
        keyboardActions = if (lastInput) KeyboardActions(onDone = { focusManager.clearFocus() }) else KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        value = userInput.value,
        onValueChange = { refresh(it) },
        label = { Text(text = "$label:") },
        visualTransformation = SingleHyphenTransformation(insertionIndex = insertionIndex)
    )
}