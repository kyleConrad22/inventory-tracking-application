package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.example.rusalqrandbarcodescanner.ScannedInfo
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@Composable
fun IncorrectBlScreen(navController: NavHostController, userInputViewModel: UserInputViewModel) {
    Scaffold(topBar = { TopAppBar(title = { Text("Incorrect Bl", textAlign = TextAlign.Center) }) }) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Incorrect BL!", modifier = Modifier.padding(16.dp))
            Text(text = "Requested BL is ${userInputViewModel.bl.value}, the scanned bundle has BL of ${ScannedInfo.blNum}",
                modifier = Modifier.padding(16.dp))
            Text(text = "Put bundle away and scan another!", modifier = Modifier.padding(16.dp))
            Button(onClick = {
                ScannedInfo.clearValues()
                navController.navigate("scannerPage")
            }) {
                Text(text = "Back to Scanner Live Feed", modifier = Modifier.padding(16.dp))
            }
        }
    }
}