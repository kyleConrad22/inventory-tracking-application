package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.viewModels.MainMenuViewModel
import com.example.rusalqrandbarcodescanner.viewModels.MainMenuViewModel.MainMenuViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@Composable
fun MainMenuScreen(navController: NavHostController) {
    val application = LocalContext.current.applicationContext
    val viewModelStoreOwner = LocalViewModelStoreOwner.current!!

    val mainMenuViewModel: MainMenuViewModel = viewModel(viewModelStoreOwner = viewModelStoreOwner, key = "mainMenuVM", factory = MainMenuViewModelFactory((application as CodeApplication).userRepository))

    val loading = mainMenuViewModel.loading.value
    val isClicked = remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text(text="Main Menu", textAlign = TextAlign.Center) }) }) {

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {

            if (loading || isClicked.value) {
                LoadingDialog(isDisplayed = loading)

            } else {
                Button(onClick = {
                    mainMenuViewModel.getIsLoad(true)
                    isClicked.value = true
                }) {
                    Text(text = "New Load",
                        modifier = Modifier
                            .padding(16.dp)
                            .size(width = 200.dp, height = 20.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center)
                }
                Button(onClick = {
                    mainMenuViewModel.getIsLoad(false)
                    isClicked.value = true
                }) {
                    Text(text = "New Reception",
                        modifier = Modifier
                            .padding(16.dp)
                            .size(width = 200.dp, height = 20.dp)
                            .align(Alignment.CenterVertically),
                        textAlign = TextAlign.Center)
                }
                Button(onClick = {
                    if (!isClicked.value) {
                        navController.navigate(Screen.ScannerScreen.title)
                    }
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
        if (!loading && isClicked.value){
            navController.navigate(Screen.InfoInputScreen.title)
            isClicked.value = false
        }
    }
}