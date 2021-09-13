package com.example.rusalqrandbarcodescanner.screens

import androidx.compose.foundation.layout.*
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
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewModels.InfoInputViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel

@Composable
fun ReceptionOptionsScreen(navController: NavHostController, scannedCodeViewModel: ScannedCodeViewModel) {
    val infoInputViewModel: InfoInputViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "infoInputVM", factory = InfoInputViewModel.InfoInputViewModelFactory((LocalContext.current.applicationContext as CodeApplication).userRepository))

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
        Text(text = "Reception Options:")

        Button(onClick = { navController.navigate(Screen.ScannerScreen.title) }) {
            Text(text = "Scan Code", modifier = Modifier
                .padding(16.dp)
                .size(width = 200.dp, height = 20.dp)
                .align(Alignment.CenterVertically), textAlign = TextAlign.Center)
        }

        if (count != null && count!! > 0) {
            Button(onClick = { navController.navigate(Screen.RemoveEntryScreen.title) }) {
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
                navController.navigateUp()
            }) {
                Text(text="Back", modifier = Modifier.padding(16.dp))
            }

            if (count != null && count!! > 0) {
                Button(onClick = {
                    navController.navigate(Screen.ReviewScreen.title)
                }) {
                    Text("Confirm Reception", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}