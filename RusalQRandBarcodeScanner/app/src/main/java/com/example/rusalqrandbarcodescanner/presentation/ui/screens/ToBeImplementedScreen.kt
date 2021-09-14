package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun ToBeImplementedScreen(navController: NavHostController) {
    Scaffold(topBar = { TopAppBar(title = { Text(text= "Not Yet Implemented", textAlign = TextAlign.Center) }) }) {
        Column(modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "This feature has yet to be implemented!")
            Button(onClick = {
                navController.popBackStack()
            }) {
                Text(text = "Back to Safety!", modifier = Modifier.padding(16.dp))
            }
        }
    }
}