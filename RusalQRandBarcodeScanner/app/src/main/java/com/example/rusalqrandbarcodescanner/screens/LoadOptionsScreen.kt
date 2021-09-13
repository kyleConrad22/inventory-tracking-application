package com.example.rusalqrandbarcodescanner.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.HttpRequestHandler
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.InfoInputViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@Composable
fun LoadOptionsScreen(navController: NavHostController, scannedCodeViewModel: ScannedCodeViewModel, userInputViewModel: UserInputViewModel, currentInventoryViewModel: CurrentInventoryViewModel) {
    val infoInputViewModel: InfoInputViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "infoInputVM", factory = InfoInputViewModel.InfoInputViewModelFactory((LocalContext.current.applicationContext as CodeApplication).userRepository))

    var resetDialog = remember { mutableStateOf(false) }
    var count by remember { mutableStateOf(scannedCodeViewModel.count.value) }
    val countObserver = Observer<Int> { it ->
        count = it
    }
    scannedCodeViewModel.count.observe(LocalLifecycleOwner.current, countObserver)

    var type by remember { mutableStateOf(infoInputViewModel.isLoad().value) }
    val typeObserver = Observer<Boolean> { it ->
        type = it
    }
    infoInputViewModel.isLoad().observe(LocalLifecycleOwner.current, typeObserver)

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        Text(text = "Load Options:")
        Text("${type.toString()}")
        Text(text = userInputViewModel.loader.value + userInputViewModel.order.value + " Load " + userInputViewModel.load.value)
        Button(onClick = { navController.navigate(Screen.ScannerScreen.title) }) {
            Text(text = "Scan Code", modifier = Modifier
                .padding(16.dp)
                .size(width = 200.dp, height = 20.dp)
                .align(Alignment.CenterVertically), textAlign = TextAlign.Center)
        }
        if (count != null && count!! > 0) {
            Button(onClick = {
                resetDialog.value = true
            }) {
                Text(text="Reset Load", modifier = Modifier
                    .padding(16.dp)
                    .size(width = 200.dp, height = 20.dp)
                    .align(Alignment.CenterVertically), textAlign = TextAlign.Center)
            }
            Button(onClick = { navController.navigate("removeEntryPage") }) {
                Text(text = "Remove Entry", modifier = Modifier
                    .padding(16.dp)
                    .size(width = 200.dp, height = 20.dp)
                    .align(Alignment.CenterVertically), textAlign = TextAlign.Center)
            }
        }
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                navController.navigate(Screen.InfoInputScreen.title)
            }) {
                Text(text="Back", modifier= Modifier.padding(16.dp))
            }
            if (count != null && count!! >0) {
                Button(onClick = {
                    HttpRequestHandler.initUpdate(scannedCodeViewModel, currentInventoryViewModel)
                    navController.navigate(Screen.ReviewScreen.title) }) {
                    Text(text = "Confirm Load", modifier = Modifier.padding(16.dp))
                }
            }
            if (resetDialog.value){
                AlertDialog(onDismissRequest = {
                    resetDialog.value = false
                }, title = {
                    Text(text="Reset Load Confirmation")
                }, text = {
                    Text(text="Are you sure you would like to remove all bundles from this load? This cannot be undone.")
                }, buttons = {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = {
                            resetDialog.value = false
                        }) {
                            Text(text="Deny Reset", modifier = Modifier.padding(16.dp))
                        }
                        Button(onClick = {
                            resetDialog.value = false
                            scannedCodeViewModel.deleteAll()
                        }) {
                            Text(text="Confirm Reset", modifier = Modifier.padding(16.dp))
                        }
                    }
                })
            }
        }
    }
}