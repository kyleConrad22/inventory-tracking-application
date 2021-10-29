package com.example.rusalqrandbarcodescanner.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.rusalqrandbarcodescanner.domain.models.SessionType

@Composable
fun ItemAddedDialog(onDismiss : () -> Unit, heat : String, sessionType : String) {
    AlertDialog(
        onDismissRequest = onDismiss, buttons = {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = onDismiss) {
                    Text(text = "Dismiss", style = MaterialTheme.typography.h6, modifier = Modifier.padding(16.dp))
                }
            }
        }, title = {
            Text(text = "Addition Confirmation")
        }, text = {
            Text(text = "$heat added to $sessionType")
        }
    )
}