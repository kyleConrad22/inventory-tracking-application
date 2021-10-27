package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.ItemActionType
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.presentation.components.loading.BasicInputDialog
import com.example.rusalqrandbarcodescanner.util.Commodity
import com.example.rusalqrandbarcodescanner.util.displayedStringPostStringInsertion
import com.example.rusalqrandbarcodescanner.util.getCommodity
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ReturnedItemViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ReturnedItemViewModel.ReturnedItemViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun ReturnedItemScreen(mainActivityVM : MainActivityViewModel, onDismissNav : () -> Unit, onReviewNav : () -> Unit) {
    val application  = LocalContext.current.applicationContext as CodeApplication

    val returnedItemVM : ReturnedItemViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "ReturnedItemVM", factory = ReturnedItemViewModelFactory(application.invRepository, mainActivityVM))

    val showAddedDialog = remember { mutableStateOf(false) }
    val showAdditionDialog = remember { mutableStateOf(false) }

    val heat = displayedStringPostStringInsertion(mainActivityVM.heatNum.value, 6, "-")
    val loading = returnedItemVM.loading.value
    val sessionType = mainActivityVM.sessionType.value
    val itemType = returnedItemVM.itemActionType.value
    val locatedItem = returnedItemVM.locatedItem.value

    if (showAddedDialog.value) {
        ItemAddedDialog(heat, mainActivityVM.sessionType.value.type, returnedItemVM.isLastItem(sessionType),
            onDismiss = {
                handleDismiss(
                    onDismiss = {
                        showAddedDialog.value = false
                        onDismissNav()
                    }, heat = mainActivityVM.heatNum
                )
            }, onAddition = {
                showAddedDialog.value = false
                if (!returnedItemVM.isLastItem(sessionType)) {
                    onDismissNav()
                } else {
                    onReviewNav()
                }
                mainActivityVM.heatNum.value = ""
            }
        )
    } else if (showAdditionDialog.value) {
        AdditionDialog(returnedItemVM = returnedItemVM,
            heat = heat,
            onBack = {
                showAdditionDialog.value = false
            }, onConfirm = {
                returnedItemVM.receiveNewItem()
                showAddedDialog.value = true
            }
        )
    } else {
        Scaffold(topBar = { TopAppBar(title = { Text("Returned Item Info") }) }) {

            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                if (loading) {
                    LoadingDialog(isDisplayed = true)
                } else {
                    when (itemType) {
                        ItemActionType.MULTIPLE_BLS_OR_PIECE_COUNTS -> MultipleBlsOrPieceCounts(uniqueComboList = returnedItemVM.uniqueList.value, heat = heat, onDismiss = { handleDismiss(onDismiss = onDismissNav, heat = mainActivityVM.heatNum) }, onConfirm = {
                            returnedItemVM.addItem()
                            showAddedDialog.value = true
                        })
                        ItemActionType.NOT_IN_LOADED_HEATS -> NotInLoadedHeats(loadedHeatList = returnedItemVM.loadedHeats.value, heat = heat, onDismiss = { handleDismiss(onDismiss = onDismissNav, heat = mainActivityVM.heatNum) })
                        ItemActionType.INCORRECT_PIECE_COUNT -> IncorrectField(field = "Piece Count", retrievedValue = locatedItem!!.quantity, requestedValue = mainActivityVM.pieceCount.value, heat = heat, onDismiss = { handleDismiss(onDismiss = onDismissNav, heat = mainActivityVM.heatNum) })
                        ItemActionType.INCORRECT_BL -> IncorrectField(field = "BL Number", retrievedValue = locatedItem!!.blNum, requestedValue = mainActivityVM.bl.value, heat = heat, onDismiss = { handleDismiss(onDismiss = onDismissNav, heat = mainActivityVM.heatNum) })
                        ItemActionType.DUPLICATE -> DuplicateItem(sessionType = sessionType, scanTime = if (sessionType == SessionType.SHIPMENT) locatedItem!!.loadTime else locatedItem!!.receptionDate, heat = heat, onDismiss = { handleDismiss(onDismiss = onDismissNav, heat =mainActivityVM.heatNum) })
                        ItemActionType.INVALID_HEAT -> InvalidHeat(sessionType = sessionType, heat = heat, onDismiss = { handleDismiss(onDismiss = onDismissNav, heat = mainActivityVM.heatNum) }, onConfirm = {
                            showAdditionDialog.value = true
                        })
                        ItemActionType.INCORRECT_BARGE -> IncorrectField(field = "Barge",
                            heat = heat,
                            retrievedValue = locatedItem!!.barge,
                            requestedValue = mainActivityVM.barge.value, onDismiss = {
                                handleDismiss(onDismiss = onDismissNav, heat =mainActivityVM.heatNum)
                            })
                        ItemActionType.VALID -> ValidHeat(item = locatedItem!!, onDismiss = { handleDismiss(onDismiss = onDismissNav, heat = mainActivityVM.heatNum) }, onConfirm = {
                            returnedItemVM.addItem()
                            showAddedDialog.value = true
                        })
                    }
                }
            }
        }
    }
}

// Displayed when user requests to add an item to reception, requests item information before confirming and adding item to inventory
@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun AdditionDialog(returnedItemVM : ReturnedItemViewModel, heat : String, onBack : () -> Unit, onConfirm : () -> Unit) {
    val focusManager  = LocalFocusManager.current

    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false), onDismissRequest = onBack) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {

                Text(text = "Heat Number: $heat")

                BasicInputDialog(
                    label = "Gross Weight",
                    userInput = returnedItemVM.scannedItemGrossWeight,
                    refresh = {
                        returnedItemVM.scannedItemGrossWeight.value = it
                        returnedItemVM.refresh()
                    },
                    focusManager = focusManager,
                    lastInput = false,
                    keyboardType = KeyboardType.Number
                )

                BasicInputDialog(
                    label = "Net Weight",
                    userInput = returnedItemVM.scannedItemNetWeight,
                    refresh = {
                        returnedItemVM.scannedItemNetWeight.value = it
                        returnedItemVM.refresh()
                    },
                    focusManager = focusManager,
                    lastInput = false,
                    keyboardType = KeyboardType.Number
                )

                BasicInputDialog(
                    label = "Quantity",
                    userInput = returnedItemVM.scannedItemQuantity,
                    refresh = {
                        returnedItemVM.scannedItemQuantity.value = it
                        returnedItemVM.refresh()
                    },
                    focusManager = focusManager,
                    lastInput = false,
                    keyboardType = KeyboardType.Number
                )

                BasicInputDialog(
                    label = "Mark",
                    userInput = returnedItemVM.scannedItemMark,
                    refresh = {
                        returnedItemVM.scannedItemMark.value = it
                        returnedItemVM.refresh()
                    }, focusManager = focusManager,
                    lastInput = true,
                    keyboardType = KeyboardType.Password
                )

                CommodityTypeInput(userInput = returnedItemVM.scannedItemGrade, onSelect = { returnedItemVM.refresh() })

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = onBack) {
                        Text(text = "Deny", modifier = Modifier.padding(16.dp))
                    }
                    if (returnedItemVM.isConfirmAdditionVisible.value) {
                        Button(onClick = onConfirm) {
                            Text(text = "Confirm", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
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

fun handleDismiss(onDismiss: () -> Unit, heat : MutableState<String>) {
    heat.value = ""
    onDismiss()
}

@Composable
private fun IncorrectField(field: String, heat: String, retrievedValue: String, requestedValue: String, onDismiss : () -> Unit) {
    Text(text = """
        Item with heat $heat cannot be added to shipment!
        Incorrect $field, returned item has a $field of $retrievedValue and the requested $field is $requestedValue.
    """.trimIndent(), modifier = Modifier.padding(16.dp))
    DismissButton {
        onDismiss()
    }
}

@Composable
private fun DuplicateItem(sessionType : SessionType, scanTime : String, heat : String, onDismiss: () -> Unit) {
    Text(text="""
       Item with heat $heat cannot be added to ${sessionType.type}!
       Item was already added ${if(sessionType == SessionType.SHIPMENT) "at" else "on"} $scanTime.
    """.trimIndent(), modifier = Modifier.padding(16.dp))
    DismissButton {
        onDismiss()
    }
}

@Composable
private fun InvalidHeat(sessionType: SessionType, heat: String, onDismiss: () -> Unit, onConfirm : () -> Unit) {
    if (sessionType == SessionType.SHIPMENT) {
        Text(text= """
            Item with heat $heat could not be found in inventory, please check the entered heat number.
            If you believe you are seeing this screen in error please notify the IT department.
        """.trimMargin(), modifier = Modifier.padding(16.dp))
        DismissButton {
            onDismiss()
        }
    } else {
        Text(text=
        """
            Item with heat $heat could not be found in inventory, would you like to receive it as a new item?
        """.trimIndent(), modifier = Modifier.padding(16.dp))
        DenyOrConfirm(onDismiss = { onDismiss() }, onConfirm = { onConfirm() })
    }
}

@Composable
private fun NotInLoadedHeats(loadedHeatList : List<String>, heat : String, onDismiss: () -> Unit) {
    Text(text = """
        Item with heat $heat cannot be added to shipment!
        Only up to three unique heats may be added to ingot shipments. The current loaded heats are:
            Heat 1: ${loadedHeatList[0]}
            Heat 2: ${loadedHeatList[1]}
            Heat 3: ${loadedHeatList[2]}
    """.trimIndent(), modifier = Modifier.padding(16.dp))
    DismissButton {
        onDismiss()
    }
}

@Composable
private fun ValidHeat(item : RusalItem, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Text(text = """
        Heat Number: ${ displayedStringPostStringInsertion(item.heatNum, 6, "-") }
        Piece Count: ${item.quantity}
        BL Number: ${item.blNum}
        Mark: ${item.mark}
    """.trimIndent() + if (getCommodity(item) == Commodity.INGOTS) "\nLot: ${item.lot}" else "")
    DenyOrConfirm(onDismiss = { onDismiss() }, onConfirm = { onConfirm() })
}

@Composable
private fun MultipleBlsOrPieceCounts(uniqueComboList : List<RusalItem>, heat : String, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Text(text="Item  with heat $heat returned multiple possible BLs / options, please ensure that the correct BL / option is being loaded.", modifier = Modifier.padding(16.dp))
    UniqueOptionsList(uniqueComboList)
    DenyOrConfirm(onDismiss = { onDismiss() }, onConfirm = { onConfirm() })
}

@Composable
private fun DismissButton(onDismiss: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = {
            onDismiss()
        }) {
            Text(text = "Deny", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun DenyOrConfirm(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly) {
        Button(onClick = {
            onDismiss()
        }) {
            Text(text = "Deny", modifier = Modifier.padding(16.dp))
        }
        Button(onClick = {
            onConfirm()
        }) {
            Text(text = "Add", modifier = Modifier.padding(16.dp))
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun ItemAddedDialog(heat : String, type : String, isLastItem : Boolean, onDismiss : () -> Unit, onAddition : () -> Unit) {
    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            onDismiss()
        }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                Text(text="Item with heat $heat has been added to the $type.", Modifier.padding(16.dp))
                Button(onClick = {
                    onAddition()
                }) {
                    Text(if (!isLastItem) {"Ok"} else { "Review $type" }, modifier = Modifier.padding(16.dp))
                }
            }

        }
    }
}

@Composable
private fun UniqueOptionsList(uniqueItems : List<RusalItem>) {
    LazyColumn(modifier = Modifier
        .background(Color.LightGray)
        .size(400.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        items(
            items = uniqueItems,
            itemContent = {
                UniqueOption(item = it)
            }
        )
    }
}

@Composable
private fun UniqueOption(item : RusalItem) {
    Card(modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 8.dp)
        .fillMaxWidth(),
        elevation = 2.dp, backgroundColor = Color.White, shape = RoundedCornerShape(corner = CornerSize(16.dp))) {
        Row {
            Column (modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.CenterVertically)) {
                Text(text="BL: ${item.blNum}", style = MaterialTheme.typography.h6)
                Text(text="Piece Count: ${item.quantity}", style = MaterialTheme.typography.h6)
            }
        }
    }
}