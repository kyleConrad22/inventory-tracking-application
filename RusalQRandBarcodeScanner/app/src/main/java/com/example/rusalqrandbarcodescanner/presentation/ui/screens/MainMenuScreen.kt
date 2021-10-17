package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@Composable
fun MainMenuScreen(navController: NavHostController, mainActivityVM: MainActivityViewModel) {

    val sessionType = mainActivityVM.sessionType.value

    Scaffold(topBar = { TopAppBar(title = { Text(text="Main Menu", textAlign = TextAlign.Center) }) }) {

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {

            Button(onClick = {
                mainActivityVM.sessionType.value = SessionType.SHIPMENT
                handleClick(navController, sessionType)
            }) {
                Text(text = "New Load",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
            Button(onClick = {
                mainActivityVM.sessionType.value = SessionType.RECEPTION
                handleClick(navController, sessionType)
            }) {
                Text(text = "New Reception",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
            Button(onClick = {
                mainActivityVM.sessionType.value = SessionType.GENERAL
                handleClick(navController, sessionType)
            }) {
                Text(text = "Get Bundle Info",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
        }
    }
}

fun handleClick(navController: NavHostController, sessionType: SessionType) {
    if (sessionType != SessionType.GENERAL) {
        navController.navigate(Screen.InfoInputScreen.title)
    } else {
        navController.navigate(Screen.ScannerScreen.title)
    }

}