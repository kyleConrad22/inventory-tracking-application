package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
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
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@Composable
fun DuplicateBundleScreen(navController: NavHostController, scanTime: String?, scannedCodeViewModel: ScannedCodeViewModel, userInputViewModel: UserInputViewModel) {
    Scaffold(topBar = { TopAppBar(title = { Text("Duplicate Bundle", textAlign = TextAlign.Center) }) }) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Bundle ${ScannedInfo.heatNum} has already been scanned!",
                modifier = Modifier.padding(16.dp))
            Text(text = "Last scan was at: $scanTime", modifier = Modifier.padding(16.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    ScannedInfo.clearValues()
                    navController.popBackStack()
                }) {
                    Text(text="Deny", modifier = Modifier.padding(16.dp))
                }
                Button(onClick = {
                    scannedCodeViewModel.insert(ScannedInfo.toScannedCode(userInputViewModel))
                }) {
                    Text(text="Add", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}