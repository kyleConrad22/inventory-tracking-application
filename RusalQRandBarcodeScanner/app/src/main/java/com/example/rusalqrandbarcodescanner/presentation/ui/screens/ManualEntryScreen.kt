package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.presentation.components.inputdialog.SingleHyphenTransformedInputDialog
import com.example.rusalqrandbarcodescanner.presentation.components.progress.SessionProgress
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ManualEntryViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun ManualEntryScreen(navController : NavHostController, mainActivityVM : MainActivityViewModel) {

    val focusManager = LocalFocusManager.current
    val manualEntryVM : ManualEntryViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "manualEntryVM", factory = ManualEntryViewModel.ManualEntryViewModelFactory(mainActivityVM))

    val displaySearchButton = manualEntryVM.displaySearchButton.value

    Scaffold(topBar = { TopAppBar(title = { Text("Manual Entry", textAlign = TextAlign.Center) }) }) {

        SessionProgress(
            sessionType = mainActivityVM.sessionType.value,
            addedItems = mainActivityVM.addedItemCount.value,
            expectedItems = if (mainActivityVM.sessionType.value == SessionType.SHIPMENT) mainActivityVM.quantity.value.toInt() else mainActivityVM.inboundItemCount.value,
            partiallyIdentifiedItems = 0,
            newItems = 0)

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {

            Text(text = "Manual Heat Number Search: ", modifier = Modifier.padding(16.dp))

            SingleHyphenTransformedInputDialog(label = "Heat Number", userInput = mainActivityVM.heatNum, refresh = {
                mainActivityVM.heatNum.value = it
                manualEntryVM.refresh()
            }, focusManager = focusManager, lastInput = true, keyBoardType = KeyboardType.Number, insertionIndex = 6)

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    navController.popBackStack()
                }) {
                    Text(text = "Back", modifier = Modifier.padding(16.dp))
                }
                if (displaySearchButton) {
                    Button(onClick = {
                        navController.navigate(Screen.ReturnedItemScreen.title)
                    }) {
                        Text(text = "Retrieve Item Info", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}
