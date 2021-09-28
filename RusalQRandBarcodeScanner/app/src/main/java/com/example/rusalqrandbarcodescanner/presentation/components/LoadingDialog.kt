package com.example.rusalqrandbarcodescanner.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingDialog(isDisplayed : Boolean) {
    Text(text= "Loading please wait...", modifier = Modifier.padding(16.dp))
    CircularIndeterminateProgressBar(isDisplayed = isDisplayed)
}