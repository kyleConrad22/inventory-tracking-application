package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@Composable
fun ConfirmationScreen(navController: NavHostController, userInputViewModel: UserInputViewModel) {
    var isLoad by remember { mutableStateOf(userInputViewModel.isLoad().value) }
    val isLoadObserver = Observer<Boolean> { it ->
        isLoad = it
    }
    userInputViewModel.isLoad().observe(LocalLifecycleOwner.current, isLoadObserver)

    Scaffold(topBar = { TopAppBar(title = { Text("Confirmation Page", textAlign = TextAlign.Center) }) }) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = if (isLoad != null && isLoad!!) {
                "Load Confirmed"
            } else {
                "Reception Confirmed"
            }, modifier = Modifier.padding(16.dp))
            Button(onClick = {
                navController.popBackStack(Screen.MainMenuScreen.title, inclusive = false)
            }) {
                Text(text = "Ok", modifier = Modifier.padding(16.dp))
            }
        }
    }
}