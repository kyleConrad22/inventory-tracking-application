package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.presentation.components.ItemAddedDialog
import com.example.rusalqrandbarcodescanner.presentation.components.StyledCardItem
import com.example.rusalqrandbarcodescanner.presentation.components.inputdialog.ValidatedInputDialog
import com.example.rusalqrandbarcodescanner.util.stringInsertion
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.NewItemViewModel

@Composable
fun NewItemScreen(onDismiss : () -> Unit, mainActivityVM: MainActivityViewModel) {
    val heat = remember { stringInsertion(mainActivityVM.heatNum.value, 6, "-") }

    val newItemVM : NewItemViewModel = viewModel(factory = NewItemViewModel.NewItemViewModelFactory(mainActivityVM, (LocalContext.current.applicationContext as CodeApplication).invRepository))
    val focusManager = LocalFocusManager.current

    var showAddedDialog by remember { mutableStateOf(false) }

    if (showAddedDialog) {
        ItemAddedDialog(
            onDismiss = {
                showAddedDialog = false
                onDismiss()
                mainActivityVM.heatNum.value = ""
            },
            heat = heat,
            sessionType = mainActivityVM.sessionType.value.toString()
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Receive New Item") }) }) {

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {

            StyledCardItem(text = "Heat Number: $heat", backgroundColor = Color.Gray)

            ValidatedInputDialog(
                label = "Gross Weight (KG)",
                userInput = newItemVM.scannedItemGrossWeight,
                refresh = {
                    newItemVM.scannedItemGrossWeight.value = it
                    newItemVM.refresh()
                },
                focusManager = focusManager,
                lastInput = false,
                keyboardType = KeyboardType.Number,
                onValidation = { newItemVM.validateGrossWeight(it) },
                isValid = newItemVM.isValidGrossWeight,
                onInvalidText = "Invalid input, gross weight must be an integer and less than 5 digits long!"
            )

            ValidatedInputDialog(
                label = "Net Weight (KG)",
                userInput = newItemVM.scannedItemNetWeight,
                refresh = {
                    newItemVM.scannedItemNetWeight.value = it
                    newItemVM.refresh()
                },
                focusManager = focusManager,
                lastInput = false,
                keyboardType = KeyboardType.Number,
                onValidation = { newItemVM.validateNetWeight(it) },
                isValid = newItemVM.isValidNetWeight,
                onInvalidText = "Invalid input, net weight must be an integer and less than 5 digits long!"
            )

            ValidatedInputDialog(
                label = "Piece Count",
                userInput = newItemVM.scannedItemQuantity,
                refresh = {
                    newItemVM.scannedItemQuantity.value = it
                    newItemVM.refresh()
                },
                focusManager = focusManager,
                lastInput = false,
                keyboardType = KeyboardType.Number,
                onValidation = { newItemVM.validateQuantity(it) },
                isValid = newItemVM.isValidQuantity,
                onInvalidText = "Invalid input, piece count must be an integer and less than 3 digits long!"
            )

            ValidatedInputDialog(
                label = "Mark",
                userInput = newItemVM.scannedItemMark,
                refresh = {
                    newItemVM.scannedItemMark.value = it
                    newItemVM.refresh()
                },
                focusManager = focusManager,
                lastInput = newItemVM.isValidMark,
                keyboardType = KeyboardType.Password,
                onValidation = { newItemVM.validateMark(it) },
                isValid = newItemVM.isValidMark,
                onInvalidText = "Invalid input, mark must be less than 30 characters long!"
            )

            CommodityTypeInput(userInput = newItemVM.scannedItemGrade,
                onSelect = { newItemVM.refresh() })

            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = onDismiss) {
                    Text(text = "Dismiss", modifier = Modifier.padding(16.dp))
                }
                if (newItemVM.isConfirmAdditionVisible.value) {
                    Button(onClick = {
                        newItemVM.receiveNewItem()
                        showAddedDialog = true
                    }) {
                        Text(text = "Confirm", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CommodityTypeCard(commodity : String, onClick: (commodity : String) -> Unit, selectedItem : String) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .selectable(
                selected = commodity == selectedItem,
                onClick = {
                    onClick(commodity)
                }
            )
            .fillMaxWidth(),
        backgroundColor =
        if (selectedItem == commodity) Color.LightGray
        else Color.White
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = commodity, style = MaterialTheme.typography.h6)
        }
    }
}

@Composable
fun CommodityTypeInput(userInput : MutableState<String>, onSelect : () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(.9f)) {
        Text(text = "Grade: ", modifier = Modifier.padding(16.dp))
        if (userInput.value !in listOf("INGOTS", "BILLETS", "")) {
            Text(text = userInput.value, modifier = Modifier.padding(16.dp))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(.9f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                items(
                    items = listOf("INGOTS", "BILLETS"),
                    itemContent = { item ->
                        CommodityTypeCard(
                            commodity = item,
                            onClick = {
                                if (item != userInput.value) userInput.value = item
                                else userInput.value = ""
                                onSelect()
                            }, selectedItem = userInput.value
                        )
                    }
                )
            }
        }
    }
}
