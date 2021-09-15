package com.example.rusalqrandbarcodescanner.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rusalqrandbarcodescanner.CircularIndeterminateProgressBar

@Composable
fun LoadingDialog(isDisplayed : Boolean) {
    Text(text= "Loading please wait...", modifier = Modifier.padding(16.dp))
    CircularIndeterminateProgressBar(isDisplayed = isDisplayed)
}