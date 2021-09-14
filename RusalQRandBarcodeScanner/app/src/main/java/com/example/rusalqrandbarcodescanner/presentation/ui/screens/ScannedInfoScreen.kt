package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.ScannedInfo
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@Composable
fun ScannedInfoScreen(navController: NavHostController, userInputViewModel: UserInputViewModel, scannedCodeViewModel: ScannedCodeViewModel) {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        Text("Scanned Bundle Information:", modifier = Modifier.padding(16.dp))
        val heatNum = ScannedInfo.heatNum
        val blNum = ScannedInfo.blNum
        Text("Heat / Cast Number: $heatNum")
        Text("BL: $blNum")
        val addType = if (userInputViewModel.loader.value != "") { "load" } else { "reception" }
        Text(text = "Add to ${addType}?", modifier = Modifier.padding(16.dp))
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                ScannedInfo.clearValues()
                navController.popBackStack()
            }) {
                Text("Deny", modifier= Modifier.padding(16.dp))
            }
            Button(onClick = {
                scannedCodeViewModel.insert(ScannedInfo.toScannedCode(userInputViewModel))
                navController.navigate(Screen.BundleAddedScreen.title)
            }) {
                Text("Add", modifier = Modifier.padding(16.dp))
            }
        }
    }
}