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
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.presentation.components.BasicInputDialog
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.presentation.components.autocomplete.AutoCompleteBox
import com.example.rusalqrandbarcodescanner.util.inputvalidation.BasicItemValidator
import com.example.rusalqrandbarcodescanner.util.inputvalidation.NameValidator
import com.example.rusalqrandbarcodescanner.util.inputvalidation.NumberValidator
import com.example.rusalqrandbarcodescanner.util.inputvalidation.WorkOrderValidator
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.InfoInputViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@ExperimentalAnimationApi
@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun InfoInputScreen(navController: NavHostController) {
    val application = LocalContext.current.applicationContext as CodeApplication

    val mainActivityVM : MainActivityViewModel = viewModel(factory = MainActivityViewModel.MainActivityViewModelFactory(application.invRepository, application))
    val infoInputVM: InfoInputViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "infoInputVM", factory = InfoInputViewModel.InfoInputViewModelFactory(mainActivityVM, application.invRepository))

    val focusManager = LocalFocusManager.current

    val isConfirmVis = infoInputVM.isConfirmVis.value
    val loading = infoInputVM.loading.value

    Scaffold(topBar = { TopAppBar(title = { Text(text = "${mainActivityVM.sessionType.value.type} Info Input", textAlign = TextAlign.Center) }) }) {

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {

            if (loading) {
                LoadingDialog(isDisplayed = true)

            } else {
                if (mainActivityVM.sessionType.value == SessionType.SHIPMENT) {

                    BasicInputDialog(label = "Work Order", userInput = mainActivityVM.workOrder, refresh = {
                        WorkOrderValidator().updateWorkOrder(it, mainActivityVM.workOrder)
                        infoInputVM.refresh()
                    }, focusManager = focusManager, lastInput = false, keyboardType = KeyboardType.Password)

                    BasicInputDialog(label = "Load", userInput = mainActivityVM.loadNum, refresh = {
                        NumberValidator().updateNumber(it, mainActivityVM.loadNum)
                        infoInputVM.refresh()
                    }, focusManager = focusManager, lastInput = false, keyboardType = KeyboardType.Number)

                    BasicInputDialog(label = "Loader", userInput = mainActivityVM.loader, refresh = {
                        NameValidator().updateName(it, mainActivityVM.loader)
                        infoInputVM.refresh()
                    }, focusManager = focusManager, lastInput = false, keyboardType = KeyboardType.Text)

                    BlInput(focusManager, infoInputVM, mainActivityVM)

                    BasicInputDialog(label = "Piece Count", userInput = mainActivityVM.pieceCount, refresh = {
                        NumberValidator().updateNumber(it, mainActivityVM.pieceCount)
                        infoInputVM.refresh()
                    }, focusManager = focusManager, lastInput = false, keyboardType = KeyboardType.Number)

                    BasicInputDialog(label = "Quantity", userInput = mainActivityVM.quantity, refresh = {
                        NumberValidator().updateNumber(it, mainActivityVM.quantity)
                        infoInputVM.refresh()
                    }, focusManager = focusManager, lastInput = true, keyboardType = KeyboardType.Number)

                } else {

                    BasicInputDialog(label = "Barge Identifier", userInput = mainActivityVM.barge, refresh = {
                        BasicItemValidator().updateItem(it, mainActivityVM.barge)
                        infoInputVM.refresh()
                    }, focusManager = focusManager, lastInput = false, keyboardType = KeyboardType.Password)

                    BasicInputDialog(label = "Checker", userInput = mainActivityVM.checker, refresh = {
                        NameValidator().updateName(it, mainActivityVM.checker)
                        infoInputVM.refresh()
                    }, focusManager = focusManager, lastInput = false, keyboardType = KeyboardType.Text)
                }

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        navController.popBackStack(Screen.MainMenuScreen.title, inclusive = false)
                    }) {
                        Text(text = "Back", modifier = Modifier.padding(14.dp))
                    }
                    if (isConfirmVis) {
                        Button(onClick = {
                            navController.navigate(Screen.OptionsScreen.title)
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

@DelicateCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun BlInput(focusManager: FocusManager, infoInputVM: InfoInputViewModel, mainActivityVM: MainActivityViewModel) {
    val blList = infoInputVM.blList.value
    AutoCompleteBox(
        items = blList,
        itemContent = { bl ->
            BlAutoCompleteItem(bl)
        }
    ) {

        val value = mainActivityVM.bl.value

        onItemSelected { bl ->
            mainActivityVM.bl.value = bl.blNumber
            filter(bl.blNumber)
            infoInputVM.refresh()
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
                BasicItemValidator().updateItem(it, mainActivityVM.bl)
                filter(it)
                infoInputVM.refresh()
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