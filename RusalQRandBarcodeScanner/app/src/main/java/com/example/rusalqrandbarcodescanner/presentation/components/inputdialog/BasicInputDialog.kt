package com.example.rusalqrandbarcodescanner.presentation.components.loading

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
fun BasicInputDialog(
    label : String,
    userInput : MutableState<String>,
    refresh : (input : String) -> Unit,
    focusManager: FocusManager,
    lastInput : Boolean,
    keyboardType : KeyboardType,
    modifier : Modifier = Modifier
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(.9f),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = keyboardType, imeAction = if (lastInput) { ImeAction.Done } else { ImeAction.Next }),
        keyboardActions =
            if (lastInput) {
                KeyboardActions(onDone = { focusManager.clearFocus() })
            } else {
                KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) })
            },
        value = userInput.value,
        onValueChange = {
            refresh(it)
        },
        label = { Text(text = "$label:") }
    )
}