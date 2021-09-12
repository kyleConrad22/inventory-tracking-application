package com.example.rusalqrandbarcodescanner.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@Composable
fun BundleInfoScreen(navController: NavHostController, barcode: String?, scannedCodeViewModel: ScannedCodeViewModel, userInputViewModel: UserInputViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current

    var scannedCode by remember { mutableStateOf(scannedCodeViewModel.findByBarcode(barcode!!).value) }
    val codeObserver = Observer<ScannedCode?> { it ->
        scannedCode = it
    }
    var count by remember { mutableStateOf(scannedCodeViewModel.count.value) }
    val countObserver = Observer<Int> { it ->
        count = it
    }
    var isLoad by remember { mutableStateOf(userInputViewModel.isLoad().value) }
    val isLoadObserver = Observer<Boolean> { it->
        isLoad = it
    }

    userInputViewModel.isLoad().observe(lifecycleOwner, isLoadObserver)
    scannedCodeViewModel.count.observe(lifecycleOwner, countObserver)
    scannedCodeViewModel.findByBarcode(barcode!!).observe(lifecycleOwner, codeObserver)

    var openDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text="Heat Number: ${scannedCode?.heatNum}")
        Text(text="BL Number: ${scannedCode?.bl}")
        Text(text="Quantity: ${scannedCode?.quantity}")
        Text(text="Net Weight Kg: ${scannedCode?.netWgtKg}")
        Text(text="Gross Weight Kg: ${scannedCode?.grossWgtKg}")
        Text(text="Barcode: $barcode")
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                navController.popBackStack()
            }) {
                Text(text = "Dismiss", modifier = Modifier.padding(16.dp))
            }
            Button(onClick = {
                openDialog = true
            }) {
                Text(text = "Remove From Shipment", modifier = Modifier.padding(16.dp))
            }
            if (openDialog) {
                AlertDialog(onDismissRequest = {
                    openDialog = false
                }, title = {
                    Text(text = "Removal Confirmation", modifier = Modifier.padding(16.dp))
                }, text = {
                    Text(text = "Are you sure you would like to remove this bundle from the load?",
                        modifier = Modifier.padding(16.dp))
                }, buttons = {
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = {
                            openDialog = false
                        }) {
                            Text(text = "Deny Removal", modifier = Modifier.padding(16.dp))
                        }
                        Button(onClick = {
                            scannedCodeViewModel.delete(scannedCode!!)
                            openDialog = false
                            if (count != null && count!! > 1) {
                                navController.navigateUp()
                            } else {
                                if (isLoad!!){
                                    navController.popBackStack(Screen.LoadOptionsScreen.title, inclusive = false)
                                } else {
                                    navController.popBackStack(Screen.ReceptionOptionsScreen.title, inclusive = false)
                                }
                            }

                        }) {
                            Text(text = "Confirm Removal", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                )
            }
        }
    }
}