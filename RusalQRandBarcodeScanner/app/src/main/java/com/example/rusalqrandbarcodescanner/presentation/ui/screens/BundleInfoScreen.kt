package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.viewModels.BundleInfoViewModel.BundleInfoViewModelFactory
import com.example.rusalqrandbarcodescanner.viewModels.BundleInfoViewModel

@Composable
fun BundleInfoScreen(navController: NavHostController, barcode: String?) {
    val application = LocalContext.current.applicationContext
    val bundleInfoViewModel : BundleInfoViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "bundleInfoViewModel", factory = BundleInfoViewModelFactory((application as CodeApplication).userRepository, application.repository))

    val code = bundleInfoViewModel.code.value
    val loading = bundleInfoViewModel.loading.value
    val isLoad = bundleInfoViewModel.isLoad.value

    bundleInfoViewModel.setValues(barcode!!)

    var openDialog by remember { mutableStateOf(false) }
    Scaffold(topBar = { TopAppBar(title = { Text("Bundle Info:") }) }) {
        Column(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally) {
            if (loading) {
                LoadingDialog(isDisplayed = true)
            } else {
                Text(text = "Heat Number: ${ code?.heatNum }")
                Text(text = "BL Number: ${ code?.bl }")
                Text(text = "Quantity: ${ code?.quantity }")
                Text(text = "Net Weight Kg: ${ code?.netWgtKg }")
                Text(text = "Gross Weight Kg: ${ code?.grossWgtKg }")
                Text(text = "Barcode: $barcode")
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        bundleInfoViewModel.resetViewModelState()
                        navController.popBackStack()
                    }) {
                        Text(text = "Dismiss", modifier = Modifier.padding(16.dp))
                    }
                    Button(onClick = {
                        openDialog = true
                    }) {
                        Text(text = "Remove From ${ if (isLoad!!) { "Load" } else { "Reception" } }", modifier = Modifier.padding(16.dp))
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
                                    /* TODO - Add check for if bundle is unidentified and if so remove from current inventory as well*/
                                    bundleInfoViewModel.removeBundle()
                                    openDialog = false
                                    bundleInfoViewModel.resetViewModelState()
                                    navController.popBackStack()
                                }) {
                                    Text(text = "Confirm Removal",
                                        modifier = Modifier.padding(16.dp))
                                }
                            }
                        }
                        )
                    }
                }
            }
        }
    }
}