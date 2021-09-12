package com.example.rusalqrandbarcodescanner.screens

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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@Composable
fun MainMenuScreen(navController: NavHostController, userInputViewModel: UserInputViewModel, scannedCodeViewModel: ScannedCodeViewModel) {
    scannedCodeViewModel.deleteAll()
    userInputViewModel.removeValues()

    Scaffold(topBar = { TopAppBar(title = { Text(text="Main Menu", textAlign = TextAlign.Center) }) }) {

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { navController.navigate(Screen.LoadInfoInputScreen.title) }) {
                Text(text = "New Load",
                    modifier = Modifier.padding(16.dp).size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
            Button(onClick = { navController.navigate("receptionOptionsPage") }) {
                Text(text = "New Reception",
                    modifier = Modifier.padding(16.dp).size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
            Button(onClick = { navController.navigate("scannerPage") }) {
                Text(text = "Get Bundle Info",
                    modifier = Modifier.padding(16.dp).size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
        }
    }
}