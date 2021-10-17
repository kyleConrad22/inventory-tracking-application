package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel

@Composable
fun OptionsScreen(navController: NavHostController) {
    val application = LocalContext.current.applicationContext as CodeApplication

    val mainActivityVM : MainActivityViewModel = viewModel(factory = MainActivityViewModel.MainActivityViewModelFactory(application.invRepository, application))

    val addedItemCount = mainActivityVM.addedItemCount.value

    val displayAdditionalOptions = addedItemCount > 0
    val displayRemoveOption = addedItemCount - Integer.parseInt(mainActivityVM.quantity.value) > 0

    val sessionType = mainActivityVM.sessionType.value

    val showResetDialog = remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text("${sessionType.type} Options", textAlign = TextAlign.Center) }) }) {

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            if (sessionType == SessionType.SHIPMENT) {
                Text(text = "${mainActivityVM.workOrder.value} Load ${mainActivityVM.loadNum.value} Shipment")
            } else {
                Text(text = "Barge ${mainActivityVM.barge.value} Reception")
            }
            Button(onClick = { navController.navigate(Screen.ScannerScreen.title) }) {
                Text(text = "Scan Code", modifier = Modifier
                    .padding(16.dp)
                    .size(width = 200.dp, height = 20.dp)
                    .align(Alignment.CenterVertically), textAlign = TextAlign.Center)
            }
            if (displayAdditionalOptions) {
                if (sessionType == SessionType.SHIPMENT) {
                    Button(onClick = {
                        showResetDialog.value = true
                    }) {
                        Text(text = "Remove All from Shipment",
                            modifier = Modifier
                                .padding(16.dp)
                                .size(width = 200.dp, height = 20.dp)
                                .align(Alignment.CenterVertically),
                            textAlign = TextAlign.Center)
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    navController.navigate(Screen.InfoInputScreen.title)
                }) {
                    Text(text = "Back", modifier = Modifier.padding(16.dp))
                }
                if (displayAdditionalOptions) {
                    Button(onClick = {
                        navController.navigate(Screen.ReviewScreen.title)
                    }) {
                        Text(text = if (displayRemoveOption) {
                            "Remove Item"
                        } else {
                            "Review ${sessionType.type}"
                        }, modifier = Modifier.padding(16.dp))
                    }
                }
                if (showResetDialog.value) {
                    AlertDialog(
                        onDismissRequest = { showResetDialog.value = false },
                        title = { Text(text = "Reset ${sessionType.type} Confirmation") },
                        text = { Text(text = "Are you sure you would like to remove all bundles from this Load? This cannot be undone.") },
                        buttons = {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly) {
                                Button(onClick = {
                                    showResetDialog.value = false
                                }) {
                                    Text(text = "Deny Reset",
                                        modifier = Modifier.padding(16.dp))
                                }
                                Button(onClick = {
                                    showResetDialog.value = false
                                    mainActivityVM.removeAllAddedItems()
                                }) {
                                    Text(text = "Confirm Reset",
                                        modifier = Modifier.padding(16.dp))
                                }
                            }
                        })
                }
            }
        }
    }
}
