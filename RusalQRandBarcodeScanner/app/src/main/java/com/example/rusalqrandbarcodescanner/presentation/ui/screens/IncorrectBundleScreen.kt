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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.util.ScannedInfo
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.viewModels.IncorrectBundleViewModel
import com.example.rusalqrandbarcodescanner.viewModels.IncorrectBundleViewModel.IncorrectBundleViewModelFactory

@Composable
fun IncorrectBundleScreen(navController: NavController) {
    val incorrectBundleViewModel : IncorrectBundleViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "incorrectBundleVM", factory = IncorrectBundleViewModelFactory((LocalContext.current.applicationContext as CodeApplication).userRepository))

    val isIncorrectBl = incorrectBundleViewModel.isIncorrectBl.value
    val loading = incorrectBundleViewModel.loading.value
    incorrectBundleViewModel.setIncorrectType()


    Scaffold(topBar = { TopAppBar(title = { Text("Incorrect Bundle", textAlign = TextAlign.Center) }) }) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            if (loading) {
                LoadingDialog(isDisplayed = true)
            } else {
                val displayText = if (isIncorrectBl!!) { "BL" } else { "quantity per bundle" }

                Text(text = "Incorrect $displayText!", modifier = Modifier.padding(16.dp))
                Text(text = "Requested $displayText is ${incorrectBundleViewModel.incorrectValue.value}, the scanned bundle has $displayText of ${if (isIncorrectBl) { ScannedInfo.blNum } else { ScannedInfo.quantity }}",
                    modifier = Modifier.padding(16.dp))
                Text(text = "Put bundle away and scan another!", modifier = Modifier.padding(16.dp))
                Button(onClick = {
                    ScannedInfo.clearValues()
                    navController.popBackStack(Screen.ScannerScreen.title, inclusive = false)
                }) {
                    Text(text = "Back to Scanner Live Feed", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}