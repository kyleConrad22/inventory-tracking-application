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
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewModels.MainMenuViewModel
import com.example.rusalqrandbarcodescanner.viewModels.MainMenuViewModel.MainMenuViewModelFactory
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@Composable
fun MainMenuScreen(navController: NavHostController) {
    val application = LocalContext.current.applicationContext
    val viewModelStoreOwner = LocalViewModelStoreOwner.current!!

    val mainMenuViewModel: MainMenuViewModel = viewModel(viewModelStoreOwner = viewModelStoreOwner, key = "mainMenuVM", factory = MainMenuViewModelFactory((application as CodeApplication).userRepository))

    Scaffold(topBar = { TopAppBar(title = { Text(text="Main Menu", textAlign = TextAlign.Center) }) }) {

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                mainMenuViewModel.isLoad(isLoad = true)
                navController.navigate(Screen.InfoInputScreen.title)
            }) {
                Text(text = "New Load",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
            Button(onClick = {
                mainMenuViewModel.isLoad(isLoad = false)

                navController.navigate(Screen.InfoInputScreen.title)
            }) {
                Text(text = "New Reception",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
            Button(onClick = { navController.navigate(Screen.ScannerScreen.title) }) {
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