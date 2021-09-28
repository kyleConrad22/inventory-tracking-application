package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.domain.models.Bl
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.presentation.components.autocomplete.AutoCompleteBox
import com.example.rusalqrandbarcodescanner.viewmodels.InfoInputViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@ExperimentalAnimationApi
@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun InfoInputScreen(navController: NavHostController) {
    val application = LocalContext.current.applicationContext
    val infoInputViewModel: InfoInputViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "infoInputVM", factory = InfoInputViewModel.InfoInputViewModelFactory((application as CodeApplication).userRepository, application.invRepository))

    val focusManager = LocalFocusManager.current

    val isConfirmVis = infoInputViewModel.isConfirmVis.value
    val isLoad = infoInputViewModel.isLoad.value
    val loading = infoInputViewModel.loading.value
    val isClicked = remember { mutableStateOf(false) }

    if (!loading && isClicked.value) {
        navController.navigate(Screen.OptionsScreen.title)
        isClicked.value = false
    }

    Scaffold(topBar = { TopAppBar(title = { Text(text = "Info Input", textAlign = TextAlign.Center) }) }) {

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {

            if (loading || isClicked.value) {
                LoadingDialog(isDisplayed = true)

            } else {
                if (isLoad) {
                    OrderInput(focusManager, infoInputViewModel)
                    LoadInput(focusManager, infoInputViewModel)
                    LoaderInput(focusManager, infoInputViewModel)
                    BlInput(focusManager, infoInputViewModel)
                    QuantityInput(focusManager, infoInputViewModel)
                    BundlesInput(focusManager, infoInputViewModel)
                } else {
                    VesselInput(focusManager, infoInputViewModel)
                    CheckerInput(focusManager, infoInputViewModel)
                }

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        if (!isClicked.value) {
                            navController.popBackStack(Screen.MainMenuScreen.title,
                                inclusive = false)
                        }
                    }) {
                        Text(text = "Back", modifier = Modifier.padding(14.dp))
                    }
                    if (isConfirmVis) {
                        Button(onClick = {
                            if (!isClicked.value) {
                                infoInputViewModel.getUpdate()
                                isClicked.value = true
                            }
                        }) {
                            Text(text = "Confirm ${
                                if (isLoad) {
                                    "Load"
                                } else {
                                    "Reception"
                                }
                            } Info",
                                modifier = Modifier.padding(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@DelicateCoroutinesApi
@Composable
fun OrderInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(.9f),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password, capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
        value = infoInputViewModel.order.value,
        onValueChange = { it ->
            infoInputViewModel.order.value = it
            infoInputViewModel.refresh()
        } , label = { Text(text="Work Order: ") })
}

@DelicateCoroutinesApi
@Composable
fun LoadInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(.9f),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
        value = infoInputViewModel.load.value,
        onValueChange = { it ->
            infoInputViewModel.load.value = it
            infoInputViewModel.refresh()
        } , label = { Text(text="Load Number: ") })
}

@DelicateCoroutinesApi
@Composable
fun LoaderInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(.9f),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
        value = infoInputViewModel.loader.value,
        onValueChange = { it ->
            infoInputViewModel.loader.value = it
            infoInputViewModel.refresh()
        } , label = { Text(text="Loader: ") })
}

@DelicateCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun BlInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    val blList = infoInputViewModel.blList.value
    AutoCompleteBox(
        items = blList,
        itemContent = { bl ->
            BlAutoCompleteItem(bl)
        }
    ) {

        val value = infoInputViewModel.bl.value

        onItemSelected { bl ->
            infoInputViewModel.bl.value = bl.blNumber
            filter(bl.blNumber)
            infoInputViewModel.refresh()
            focusManager.moveFocus(FocusDirection.Down)
        }
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(.9f)
                .onFocusChanged { focusState ->
                    isSearching = focusState.isFocused
                },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password, capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            value = value,
            onValueChange = {  it ->
                infoInputViewModel.bl.value = it
                filter(it)
                infoInputViewModel.refresh()
            },
            label = { Text(text="BL Number: ") })
    }
}

@Composable
fun BlAutoCompleteItem(bl : Bl) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text=bl.blNumber, style = MaterialTheme.typography.subtitle2)
    }
}

@DelicateCoroutinesApi
@Composable
fun QuantityInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(.9f),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
        value = infoInputViewModel.quantity.value,
        onValueChange = { it ->
            infoInputViewModel.quantity.value = it
            infoInputViewModel.refresh()
        } , label = { Text(text="Piece Count: ") })
}

@DelicateCoroutinesApi
@Composable
fun BundlesInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(.9f),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true)}),
        value = infoInputViewModel.bundles.value,
        onValueChange = { it ->
            infoInputViewModel.bundles.value = it
            infoInputViewModel.refresh()
        } , label = { Text(text="Bundle Quantity: ") })
}

@DelicateCoroutinesApi
@Composable
fun VesselInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(.9f),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
        value = infoInputViewModel.vessel.value,
        onValueChange = { it ->
            infoInputViewModel.vessel.value = it
            infoInputViewModel.refresh()
        }, label = { Text(text = "Vessel:") })
}

@DelicateCoroutinesApi
@Composable
fun CheckerInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(.9f),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(KeyboardCapitalization.Characters, imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) }),
        value = infoInputViewModel.checker.value,
        onValueChange = { it ->
            infoInputViewModel.checker.value = it
            infoInputViewModel.refresh()
        }, label = { Text("Checker:") })
}