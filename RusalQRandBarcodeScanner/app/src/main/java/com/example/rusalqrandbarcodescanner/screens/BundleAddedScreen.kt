package com.example.rusalqrandbarcodescanner.screens

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
import com.example.rusalqrandbarcodescanner.ScannedInfo
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@Composable
fun BundleAddedScreen(navController: NavHostController, scannedCodeViewModel: ScannedCodeViewModel, userInputViewModel: UserInputViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var count = remember { scannedCodeViewModel.count.value }
    val countObserver = Observer<Int> { it ->
        count = it
    }
    var isLoad by remember { mutableStateOf(userInputViewModel.isLoad().value) }
    val isLoadObserver = Observer<Boolean> { it ->
        isLoad = it
    }
    scannedCodeViewModel.count.observe(lifecycleOwner, countObserver)
    userInputViewModel.isLoad().observe(lifecycleOwner, isLoadObserver)

    val type = if(isLoad != null && isLoad!!) { "load" } else{ "reception" }

    Scaffold(topBar = { TopAppBar( title = { Text("Bundle Added", textAlign = TextAlign.Center) }) }) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Bundle ${ScannedInfo.heatNum} added to $type")

            if (count != null) {
                Text(text = "Bundles Remaining: ${Integer.parseInt(userInputViewModel.bundles.value!!) - count!!}")
                val dest = if (Integer.parseInt(userInputViewModel.bundles.value!!) - count!! == 0) {
                    Screen.LoadReviewScreen.title
                } else {
                    Screen.ScannerScreen.title
                }
                Button(onClick = {
                    if (Integer.parseInt(userInputViewModel.bundles.value!!) - count!! == 0) {
                        navController.navigate(Screen.LoadReviewScreen.title)
                    } else {
                        ScannedInfo.clearValues()
                        navController.popBackStack(Screen.ScannerScreen.title, inclusive = false)
                    } }) {
                    Text(text = "OK", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}