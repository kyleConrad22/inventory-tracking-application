package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.ScannedInfo
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@ExperimentalComposeUiApi
@Composable
fun ManualEntryScreen(navController: NavHostController, userInputViewModel: UserInputViewModel, currentInventoryViewModel: CurrentInventoryViewModel, scannedCodeViewModel: ScannedCodeViewModel) {
    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var openDialog = remember { mutableStateOf(false) }

    var uiHeat by remember { mutableStateOf(userInputViewModel.heat.value) }
    val heatObserver = Observer<String> { it ->
        uiHeat = it
    }
    userInputViewModel.heat.observe(lifecycleOwner, heatObserver)

    var blList by remember { mutableStateOf(currentInventoryViewModel.getBlList(uiHeat).value) }
    val blListObserver = Observer<List<String>?> { it ->
        blList = it
    }
    currentInventoryViewModel.getBlList(uiHeat).observe(lifecycleOwner, blListObserver)

    var quantList by remember { mutableStateOf(currentInventoryViewModel.getQuantList(uiHeat).value) }
    val quantListObserver = Observer<List<String>?> { it ->
        quantList = it
    }
    currentInventoryViewModel.getQuantList(uiHeat).observe(lifecycleOwner, quantListObserver)

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        Text(text="Manual Heat Number Search: ", modifier = Modifier.padding(16.dp))
        HeatNumberInput(focusManager, userInputViewModel)
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                navController.navigateUp()
            }) {
                Text(text = "Back", modifier = Modifier.padding(16.dp))
            }
            if (uiHeat != null && uiHeat!!.length >= 6) {
                Button(onClick = {
                    if (uiHeat?.length == 6) {
                        var result: List<CurrentInventoryLineItem>?
                        currentInventoryViewModel.findByBaseHeat("%${uiHeat!!}%")
                            .observe(lifecycleOwner, { returnedVal ->
                                result = returnedVal

                                if (result != null) {
                                    if (blList!!.size == 1 && quantList!!.size == 1) {
                                        currentInventoryViewModel.addNewItemByBaseHeat(uiHeat!!, userInputViewModel).observe(lifecycleOwner){
                                            if (it) { Log.d("DEBUG", "Successfully set new item") }
                                        }
                                        navController.navigate(Screen.ScannedInfoScreen.title)
                                    } else if (blList!!.size > 1) {
                                        /*TODO*/
                                        /*Make BL List Clickable with the requested bl being sent to scanned info return*/
                                        navController.navigate(Screen.BlOptionsScreen.title)
                                        /*Present bl options to loader and ask for them to make a selection*/

                                    } else if (quantList!!.size > 1) {
                                        /*TODO*/
                                        navController.navigate(Screen.QuantityOptionsScreen.title)
                                        /*Ask for loader to verify that there are the requested number of pieces on this bundle, have them type the amount*/

                                    } else {
                                        openDialog.value = true
                                    }
                                } else {
                                    openDialog.value = true
                                }
                            })
                    } else {
                        var returnedCode: ScannedCode?
                        scannedCodeViewModel.findByHeat(uiHeat!!)
                            .observe(lifecycleOwner, { code ->
                                returnedCode = code
                                if (returnedCode == null) {
                                    currentInventoryViewModel.findByHeat(uiHeat!!)
                                        .observe(lifecycleOwner, { inventoryItem ->
                                            if (inventoryItem != null) {
                                                ScannedInfo.getValues(inventoryItem)
                                                Log.d("DEBUG", "Retrieved non-null reference")
                                                if (ScannedInfo.blNum == userInputViewModel.bl.value && ScannedInfo.quantity == userInputViewModel.quantity.value) {
                                                    navController.navigate(Screen.ScannedInfoScreen.title)
                                                } else if (ScannedInfo.blNum != userInputViewModel.bl.value) {
                                                    navController.navigate(Screen.IncorrectBlScreen.title)
                                                } else {
                                                    navController.navigate(Screen.IncorrectQuantityScreen.title)
                                                }
                                            } else {
                                                openDialog.value = true
                                            }
                                        })
                                } else if (returnedCode?.scanTime != null) {
                                    navController.navigate("${Screen.DuplicateBundleScreen.title}/${returnedCode?.scanTime}")
                                }
                            })
                    }
                }) {
                    Text(text = "Retrieve Bundle Info", modifier = Modifier.padding(16.dp))
                }
                if (openDialog.value) {
                    AlertDialog(onDismissRequest = {
                        openDialog.value = false
                    }, buttons = {
                        Button(onClick = {
                            openDialog.value = false
                        }, modifier = Modifier
                            .align(Alignment.CenterVertically)) {
                            Text(text = "Dismiss", modifier = Modifier.padding(16.dp))
                        }
                    }, title = {
                        Text("Invalid Heat Number")
                    }, text = {
                        Text("The given heat number was not found in the system!")
                    })
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