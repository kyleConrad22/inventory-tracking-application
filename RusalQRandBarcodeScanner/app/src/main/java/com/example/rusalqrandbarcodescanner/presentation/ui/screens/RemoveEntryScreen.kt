package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@ExperimentalComposeUiApi
@Composable
fun RemoveEntryScreen(navController: NavHostController, userInputViewModel: UserInputViewModel, scannedCodeViewModel: ScannedCodeViewModel) {
    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val openDialog = remember { mutableStateOf(false) }
    val removalDialog = remember { mutableStateOf(false) }

    var uiHeat by remember { mutableStateOf(userInputViewModel.heat.value) }
    val uiHeatObserver = Observer<String> { it ->
        uiHeat = it
    }

    var isLoad by remember { mutableStateOf(userInputViewModel.isLoad().value) }
    val isLoadObserver = Observer<Boolean> { it ->
        isLoad = it
    }

    var code by remember { mutableStateOf(scannedCodeViewModel.findByHeat(uiHeat!!).value) }
    val codeObserver = Observer<ScannedCode?> { it ->
        code = it
    }

    userInputViewModel.isLoad().observe(lifecycleOwner, isLoadObserver)
    userInputViewModel.heat.observe(lifecycleOwner, uiHeatObserver)
    scannedCodeViewModel.findByHeat(uiHeat!!).observe(lifecycleOwner, codeObserver)

    MaterialTheme {
        Scaffold(topBar = { TopAppBar(title = { Text("Remove Entry", textAlign = TextAlign.Center) }) }) {
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                Text(text = "Enter Heat / Cast Number of Entry to be Removed:",
                    modifier = Modifier.padding(16.dp))
                HeatNumberInput(focusManager, userInputViewModel)
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        if (isLoad!!) {
                            navController.popBackStack("loadOptionsPage", inclusive = false)
                        } else {
                            navController.popBackStack("receptionOptionsPage", inclusive = false)
                        }
                    }) {
                        Text(text = "Back", modifier = Modifier.padding(16.dp))
                    }
                    if (uiHeat != null && uiHeat!!.length >= 6) {
                        Button(onClick = {
                            if (code != null) {
                                removalDialog.value = true
                            } else {
                                openDialog.value = true
                            }
                        }) {
                            Text(text = "Confirm Removal", modifier = Modifier.padding(16.dp))
                        }
                        if (removalDialog.value) {
                            AlertDialog(onDismissRequest = {
                                removalDialog.value = false
                            }, title = {
                                Text(text = "Confirm Removal", modifier = Modifier.padding(16.dp))
                            }, text = {
                                Text(text = "Are you sure you would like to remove the bundle given by $uiHeat?",
                                    modifier = Modifier.padding(16.dp))
                            }, buttons = {
                                Row(modifier = Modifier.fillMaxWidth()) {
                                    Button(onClick = {
                                        removalDialog.value = false
                                    }) {
                                        Text(text = "Deny Removal")
                                    }
                                    Button(onClick = {
                                        removalDialog.value = false
                                        scannedCodeViewModel.delete(code!!)
                                        userInputViewModel.heat.value = ""
                                    }) {
                                        Text(text = "Confirm Removal")
                                    }
                                }
                            }
                            )
                        }
                        if (openDialog.value) {
                            AlertDialog(
                                onDismissRequest = {
                                    openDialog.value = false
                                }, title = {
                                    Text(text = "Invalid Heat Number")
                                }, text = {
                                    Text("The given heat number was not found in the system!")
                                }, buttons = {
                                    Button(onClick = { openDialog.value = false },
                                        modifier = Modifier.align(Alignment.CenterVertically)) {
                                        Text(text = "Dismiss", modifier = Modifier.padding(16.dp))
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun HeatNumberInput(focusManager: FocusManager, userInputViewModel: UserInputViewModel) {
    var heat by remember { mutableStateOf(userInputViewModel.heat.value) }
    val heatObserver = Observer<String>{ it ->
        heat = it
    }
    userInputViewModel.heat.observe(LocalLifecycleOwner.current, heatObserver)

    heat?.let { heatIt ->
        OutlinedTextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus(true) }),
            value = heatIt,
            onValueChange = { it ->
                userInputViewModel.heat.value = it
                userInputViewModel.refresh() },
            label = { Text(text = "Heat Number: ") })
    }
}