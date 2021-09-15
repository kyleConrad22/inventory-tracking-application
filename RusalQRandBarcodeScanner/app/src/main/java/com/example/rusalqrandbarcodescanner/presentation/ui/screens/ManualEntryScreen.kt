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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.navArgument
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.ScannedInfo
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ManualEntryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ManualEntryViewModel.ManualEntryViewModelFactory
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun ManualEntryScreen(navController: NavHostController, userInputViewModel: UserInputViewModel, currentInventoryViewModel: CurrentInventoryViewModel, scannedCodeViewModel: ScannedCodeViewModel) {
    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val application = LocalContext.current.applicationContext

    val manualEntryViewModel : ManualEntryViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "manualEntryVM", ManualEntryViewModelFactory((application as CodeApplication).userRepository, application.repository, application.invRepository))

    val isSearchVis = manualEntryViewModel.isSearchVis.value
    val openDialog = remember { mutableStateOf(false) }

    var isBaseHeat by remember { mutableStateOf(false) }
    val isBaseHeatObserver = Observer<Boolean> { it ->
        isBaseHeat = it
    }
    manualEntryViewModel.isBaseHeat.observe(lifecycleOwner, isBaseHeatObserver)

    var heat by remember { mutableStateOf(userInputViewModel.heat.value) }
    val heatObserver = Observer<String> { it ->
        heat = it
    }
    manualEntryViewModel.heat.observe(lifecycleOwner, heatObserver)

    val loading = manualEntryViewModel.loading.value
    val isClicked = remember { mutableStateOf(false) }

    val destination = manualEntryViewModel.destination.value

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        if (loading && isClicked.value) {
            LoadingDialog(isDisplayed = true)
        } else {
            Text(text = "Manual Heat Number Search: ", modifier = Modifier.padding(16.dp))
            HeatNumberInput(focusManager, manualEntryViewModel)
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    navController.navigateUp()
                }) {
                    Text(text = "Back", modifier = Modifier.padding(16.dp))
                }
                if (isSearchVis) {
                    Button(onClick = {
                        if (isBaseHeat) {
                            manualEntryViewModel.setDestination()
                            isClicked.value = true

                            /*
                            var returnType : String? = ""
                            GlobalScope.launch(Dispatchers.Main) { returnType = manualEntryViewModel.getReturnType() }

                            if (returnType != "Null") {
                                when (returnType) {
                                    "Single Return" -> {
                                        destination = Screen.ScannedInfoScreen.title
                                    }
                                    "Multiple Bls" -> {
                                        /*TODO*/
                                        destination = Screen.BlOptionsScreen.title
                                        /*Present bl options to loader and ask for them to confirm BL being loaded is correct*/

                                    }
                                    "Multiple Quantities" -> {
                                        /*TODO*/
                                        destination = Screen.QuantityOptionsScreen.title
                                        /*Ask for loader to verify that there are the requested number of pieces on this bundle, have them type the amount*/

                                    }
                                    "Multiple Bls and Quantities" -> {
                                        /*TODO*/
                                        destination = Screen.ToBeImplementedScreen.title
                                        /*Ask for loader to verify requested amount of pieces and confirm that the BL being loaded is correct.*/
                                    }
                                    isClicked.value = true
                             */
                        } else {
                            var returnedCode: ScannedCode?
                            scannedCodeViewModel.findByHeat(heat!!)
                                .observe(lifecycleOwner, { code ->
                                    returnedCode = code
                                    if (returnedCode == null) {
                                        currentInventoryViewModel.findByHeat(heat!!)
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
    if (!loading && isClicked.value) {
        if (destination == "N/A") {
            openDialog.value = true
            isClicked.value = false
        } else {
            isClicked.value = false
            navController.navigate(destination)
        }
    }
}

@Composable
private fun HeatNumberInput(focusManager : FocusManager, manualEntryViewModel : ManualEntryViewModel) {
    var heat by remember { mutableStateOf(manualEntryViewModel.heat.value) }
    val heatObserver = Observer<String>{ it ->
        heat = it
    }
    manualEntryViewModel.heat.observe(LocalLifecycleOwner.current, heatObserver)

    heat?.let { heatIt ->
        OutlinedTextField(
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus(true) }),
            value = heatIt,
            onValueChange = { it ->
                manualEntryViewModel.heat.value = it
                manualEntryViewModel.refresh() },
            label = { Text(text = "Heat Number: ") })
    }
}