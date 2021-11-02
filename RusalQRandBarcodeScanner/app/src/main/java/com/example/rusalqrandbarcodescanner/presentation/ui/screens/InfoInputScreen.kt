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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.domain.models.Barge
import com.example.rusalqrandbarcodescanner.domain.models.Bl
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.presentation.components.StyledCardItem
import com.example.rusalqrandbarcodescanner.presentation.components.inputdialog.SingleHyphenTransformedInputDialog
import com.example.rusalqrandbarcodescanner.presentation.components.autocomplete.AutoCompleteBox
import com.example.rusalqrandbarcodescanner.presentation.components.inputdialog.ValidatedInputDialog
import com.example.rusalqrandbarcodescanner.util.inputvalidation.BasicItemValidator
import com.example.rusalqrandbarcodescanner.util.inputvalidation.WorkOrderValidator
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.InfoInputViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@ExperimentalAnimationApi
@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun InfoInputScreen(mainActivityVM: MainActivityViewModel, onBack : () -> Unit, onConfirm : () -> Unit) {
    val application = LocalContext.current.applicationContext as CodeApplication

    val infoInputVM: InfoInputViewModel = viewModel(factory = InfoInputViewModel.InfoInputViewModelFactory(mainActivityVM, application.invRepository))

    val focusManager = LocalFocusManager.current

    val loading = infoInputVM.loading.value

    Scaffold(topBar = { TopAppBar(title = { Text(text = "${mainActivityVM.sessionType.value.type} Info Input", textAlign = TextAlign.Center) }) }) {

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {

            if (loading) {
                LoadingDialog(isDisplayed = true)

            } else {
                if (mainActivityVM.addedItemCount.value == 0)
                    EditableFields(sessionType = mainActivityVM.sessionType.value,
                        focusManager = focusManager,
                        workOrderState = mainActivityVM.workOrder,
                        loadNumberState = mainActivityVM.loadNum,
                        loaderState = mainActivityVM.loader,
                        pieceCountState = mainActivityVM.pieceCount,
                        quantityState = mainActivityVM.quantity,
                        checkerState = mainActivityVM.checker,
                        bargeIdentifierState = mainActivityVM.barge,
                        blNumberState = mainActivityVM.bl,
                        blList = infoInputVM.blList.value,
                        bargeList = infoInputVM.bargeList.value,
                        onChange = { infoInputVM.refresh() },
                        infoInputVM = infoInputVM
                    )
                else
                    NonEditableFields(sessionType = mainActivityVM.sessionType.value,
                        workOrder = mainActivityVM.workOrder.value,
                        loadNumber = mainActivityVM.loadNum.value,
                        loader = mainActivityVM.loader.value,
                        pieceCount = mainActivityVM.pieceCount.value,
                        quantity = mainActivityVM.quantity.value,
                        blNumber = mainActivityVM.bl.value,
                        checker = mainActivityVM.checker.value,
                        bargeIdentifier = mainActivityVM.barge.value)

                if (infoInputVM.displayButtons) {
                    Row(modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        Button(onClick = onBack) {
                            Text(text = "Back", modifier = Modifier.padding(14.dp))
                        }
                        if (infoInputVM.displayConfirmButton) {
                            Button(onClick = {
                                mainActivityVM.refresh()
                                if (infoInputVM.onConfirmValidator()) {
                                    onConfirm()
                                }
                            }) {
                                Text(text = "Confirm ${mainActivityVM.sessionType.value.type} Info",
                                    modifier = Modifier.padding(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

@DelicateCoroutinesApi
@ExperimentalAnimationApi
@Composable
private fun EditableFields(sessionType : SessionType, focusManager : FocusManager, workOrderState : MutableState<String>, loadNumberState : MutableState<String>, loaderState : MutableState<String>, pieceCountState : MutableState<String>, quantityState : MutableState<String>, checkerState : MutableState<String>, bargeIdentifierState : MutableState<String>, blNumberState : MutableState<String>, blList : List<Bl>, bargeList : List<Barge>, onChange : () -> Unit,
    infoInputVM : InfoInputViewModel
) {

    if (sessionType == SessionType.SHIPMENT) {

        SingleHyphenTransformedInputDialog(label = "Work Order", userInput = workOrderState, refresh = {
            WorkOrderValidator().updateWorkOrder(it, workOrderState)
            onChange()
        }, focusManager = focusManager,
        lastInput = false,
        keyBoardType = KeyboardType.Password,
        insertionIndex = 3
        )

        ValidatedInputDialog(
            label = "Load Number",
            userInput = loadNumberState,
            refresh = {
                loadNumberState.value = it
                onChange()
            },
            focusManager = focusManager,
            lastInput = false,
            keyboardType = KeyboardType.Number,
            onValidation = {
                infoInputVM.validateLoad(it)
            }, isValid = infoInputVM.isValidLoad,
            onInvalidText = "Invalid input, load number must be less than 3 digits long!"
        )

        ValidatedInputDialog(
            label = "Loader Name",
            userInput = loaderState,
            refresh = {
                loaderState.value = it
                onChange()
            }, focusManager = focusManager,
            lastInput = false,
            keyboardType = KeyboardType.Text,
            onValidation = {
                infoInputVM.validateLoader(it)
            }, isValid = infoInputVM.isValidLoader,
            onInvalidText = "Invalid input, name must be an less than 30 characters long!"
        )

        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            if (!infoInputVM.isValidBl) {
                Text(text = "Invalid input, BL number must be selected from provided drop-down menu!", color = Color.Red, modifier = Modifier.padding(8.dp))
            }
            BlInput(focusManager = focusManager,
                blList = blList,
                blNumberState = blNumberState,
                onChange = onChange)
        }

        ValidatedInputDialog (
            label = "Piece Count",
            userInput = pieceCountState,
            refresh = {
                pieceCountState.value = it
                onChange()
            }, focusManager = focusManager,
            lastInput = false,
            keyboardType = KeyboardType.Number,
            onValidation = {
                infoInputVM.validatePieceCount(it)
            }, isValid = infoInputVM.isValidPieceCount,
            onInvalidText = "Invalid input, piece count must be an integer and less than 3 characters long!"
        )

        ValidatedInputDialog(
            label = "Quantity",
            userInput = quantityState,
            refresh = {
                quantityState.value = it
                onChange()
            },
            focusManager = focusManager,
            lastInput = true,
            keyboardType = KeyboardType.Number,
            onValidation = { infoInputVM.validateQuantity(it) },
            isValid = infoInputVM.isValidQuantity,
            onInvalidText = "Invalid input, quantity must be an integer!"
        )

    } else {

        Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            if (!infoInputVM.isValidBarge) {
                Text(text = "Invalid input, barge identifier must be selected from provided drop-down menu!", color = Color.Red, modifier = Modifier.padding(8.dp))
            }
            BargeInput(focusManager = focusManager,
                bargeList = bargeList,
                bargeIdentifierState = bargeIdentifierState,
                onChange = onChange)

        }
        ValidatedInputDialog(
            label = "Checker Name",
            userInput = checkerState,
            refresh = {
                checkerState.value = it
                onChange()
            }, focusManager = focusManager,
            lastInput = true,
            keyboardType = KeyboardType.Text,
            onValidation = {
               infoInputVM.validateChecker(it)
            }, isValid = infoInputVM.isValidChecker,
            onInvalidText = "Invalid input, name must be less than 30 characters long!"
        )

    }
}

@Composable
private fun NonEditableFields(sessionType : SessionType, workOrder : String, loadNumber : String, loader : String, pieceCount : String, quantity : String, blNumber : String, checker : String, bargeIdentifier : String) {
    if (sessionType == SessionType.SHIPMENT) {

        StyledCardItem(text = "Work Order: $workOrder", backgroundColor = Color.LightGray)
        StyledCardItem(text = "Load Number: $loadNumber", backgroundColor = Color.LightGray)
        StyledCardItem(text = "Loader: $loader", backgroundColor = Color.LightGray)
        StyledCardItem(text = "BL Number: $blNumber", backgroundColor = Color.LightGray)
        StyledCardItem(text = "Piece Count: $pieceCount", backgroundColor = Color.LightGray)
        StyledCardItem(text = "Quantity: $quantity", backgroundColor = Color.LightGray)

    } else {

        StyledCardItem(text = "Barge Identifier: $bargeIdentifier", backgroundColor = Color.LightGray)
        StyledCardItem(text = "Checker: $checker", backgroundColor = Color.LightGray)

    }
}

@DelicateCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun BlInput(focusManager: FocusManager, blList : List<Bl>, blNumberState : MutableState<String>, onChange : () -> Unit) {
    AutoCompleteBox(
        items = blList,
        itemContent = { bl ->
            AutoCompleteItem(bl.text)
        }
    ) {

        val value = blNumberState.value

        onItemSelected { bl ->
            blNumberState.value = bl.text
            filter(bl.text)
            onChange()
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
                BasicItemValidator().updateItem(it, blNumberState)
                filter(it)
                onChange()
            },
            label = { Text(text="BL Number: ") })
    }
}

@ExperimentalAnimationApi
@DelicateCoroutinesApi
@Composable
fun BargeInput(focusManager: FocusManager, bargeList : List<Barge>, bargeIdentifierState : MutableState<String>, onChange : () -> Unit) {
    AutoCompleteBox(
        items = bargeList,
        itemContent = { barge ->
            AutoCompleteItem(text = barge.text)
        }
    ) {
        val value = bargeIdentifierState.value

        onItemSelected { barge ->
            bargeIdentifierState.value = barge.text
            filter(barge.text)
            onChange()
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
            onValueChange = {
                BasicItemValidator().updateItem(it, bargeIdentifierState)
                filter(it)
                onChange()
            },
            label= { Text(text="Barge Identifier: ") }
        )
    }
}

@Composable
fun AutoCompleteItem(text : String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text=text, style = MaterialTheme.typography.subtitle2)
    }
}

