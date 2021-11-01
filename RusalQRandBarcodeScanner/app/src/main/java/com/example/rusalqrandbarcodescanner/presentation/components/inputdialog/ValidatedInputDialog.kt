package com.example.rusalqrandbarcodescanner.presentation.components.inputdialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.rusalqrandbarcodescanner.presentation.components.loading.BasicInputDialog

@Composable
fun ValidatedInputDialog(
    label : String,
    userInput : MutableState<String>,
    refresh : (input : String) -> Unit,
    focusManager : FocusManager,
    lastInput : Boolean,
    keyboardType : KeyboardType,
    onValidation : (input : String) -> Unit,
    isValid : Boolean,
    onInvalidText : String
) {
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        if (!isValid) Text(text = onInvalidText, color = Color.Red, modifier = Modifier.padding(8.dp))
        BasicInputDialog(
            label = label,
            userInput = userInput,
            refresh = {
                onValidation(it)
                refresh(it)
            }, focusManager = focusManager,
            lastInput = lastInput,
            keyboardType = keyboardType,
        )
    }
}