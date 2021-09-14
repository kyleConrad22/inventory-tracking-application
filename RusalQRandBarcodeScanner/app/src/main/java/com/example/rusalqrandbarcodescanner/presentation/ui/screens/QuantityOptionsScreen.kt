package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.example.rusalqrandbarcodescanner.ScannedInfo
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@Composable
fun QuantityOptionsScreen(navController: NavController, currentInventoryViewModel: CurrentInventoryViewModel, userInputViewModel: UserInputViewModel, scannedCodeViewModel: ScannedCodeViewModel) {
    val heat = userInputViewModel.heat.value
    val quantity = userInputViewModel.quantity.value
    var blList by remember { mutableStateOf(currentInventoryViewModel.getBlList(heat).value) }
    val blListObserver = Observer<List<String>?>{ it ->
        blList = it
    }
    currentInventoryViewModel.getBlList(heat).observe(LocalLifecycleOwner.current, blListObserver)

    if (blList != null) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = """
                    Heat $heat is available in multiple quantities for BL ${blList!![0]}!
                    Ensure that the bundle being loaded is of quantity $quantity!
                    Would you like to add this bundle to the load?
                    """.trimMargin(), modifier = Modifier.padding(16.dp))
            Button(onClick = {
                navController.popBackStack()
            }) {
                Text(text="Back", modifier = Modifier.padding(16.dp))
            }
            Button(onClick = {
                scannedCodeViewModel.insert(ScannedInfo.toScannedCode(userInputViewModel))
                navController.navigate(Screen.BundleAddedScreen.title)
            }) {
                Text("Add", modifier = Modifier.padding(16.dp))
            }
        }
    }
}