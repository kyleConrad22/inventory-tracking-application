package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.ItemActionType
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
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
fun ReturnedItemScreen(navController: NavHostController, mainActivityVM : MainActivityViewModel) {
    val application  = LocalContext.current.applicationContext as CodeApplication

    val returnedItemVM : ReturnedItemViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "ReturnedItemVM", factory = ReturnedItemViewModelFactory(application.invRepository, mainActivityVM))

    val showAddedDialog = remember { mutableStateOf(false) }

    val heat = displayedStringPostStringInsertion(mainActivityVM.heatNum.value, 6, "-")
    val loading = returnedItemVM.loading.value
    val sessionType = mainActivityVM.sessionType.value
    val itemType = returnedItemVM.itemActionType.value
    val locatedItem = returnedItemVM.locatedItem.value

    if (showAddedDialog.value) {
        ItemAddedDialog(navController, showAddedDialog, heat, mainActivityVM.sessionType.value.type, returnedItemVM.isLastItem(sessionType), onDismiss = { mainActivityVM.heatNum.value = "" })
    } else {
        Scaffold(topBar = { TopAppBar(title = { Text("Returned Item Info") }) }) {

            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                if (loading) {
                    LoadingDialog(isDisplayed = true)
                } else {
                    when (itemType) {
                        ItemActionType.MULTIPLE_BLS_OR_PIECE_COUNTS -> MultipleBlsOrPieceCounts(uniqueComboList = returnedItemVM.uniqueList.value, heat = heat, onDismiss = { handleDismiss(navController, mainActivityVM.heatNum) }, onConfirm = {
                            returnedItemVM.addItem()
                            showAddedDialog.value = true
                        })
                        ItemActionType.NOT_IN_LOADED_HEATS -> NotInLoadedHeats(loadedHeatList = returnedItemVM.loadedHeats.value, heat = heat, onDismiss = { handleDismiss(navController, mainActivityVM.heatNum) })
                        ItemActionType.INCORRECT_PIECE_COUNT -> IncorrectField(field = "Piece Count", retrievedValue = locatedItem!!.quantity, requestedValue = mainActivityVM.pieceCount.value, heat = heat, onDismiss = { handleDismiss(navController, mainActivityVM.heatNum) })
                        ItemActionType.INCORRECT_BL -> IncorrectField(field = "BL Number", retrievedValue = locatedItem!!.blNum, requestedValue = mainActivityVM.bl.value, heat = heat, onDismiss = { handleDismiss(navController, mainActivityVM.heatNum) })
                        ItemActionType.DUPLICATE -> DuplicateItem(sessionType = sessionType, scanTime = if (sessionType == SessionType.SHIPMENT) locatedItem!!.loadTime else locatedItem!!.receptionDate, heat = heat, onDismiss = { handleDismiss(navController, mainActivityVM.heatNum) })
                        ItemActionType.INVALID_HEAT -> InvalidHeat(sessionType = sessionType, heat = heat, onDismiss = { handleDismiss(navController, mainActivityVM.heatNum) }, onConfirm = {
                            handleAddition()
                            showAddedDialog.value = true
                        })
                        ItemActionType.INCORRECT_BARGE -> IncorrectField(field = "Barge",
                            heat = heat,
                            retrievedValue = locatedItem!!.barge,
                            requestedValue = mainActivityVM.barge.value, onDismiss = {
                                handleDismiss(navController, mainActivityVM.heatNum)
                            })
                        ItemActionType.VALID -> ValidHeat(item = locatedItem!!, onDismiss = { handleDismiss(navController, mainActivityVM.heatNum) }, onConfirm = {
                            returnedItemVM.addItem()
                            showAddedDialog.value = true
                        })
                    }
                }
            }
        }
    }
}

/*TODO - Allow user to manually enter data / scan data to retrieve item information*/
fun handleAddition() {

}

fun handleDismiss(navController: NavHostController, heat : MutableState<String>) {
    heat.value = ""
    navController.popBackStack(Screen.OptionsScreen.title, inclusive = false)
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
private fun ItemAddedDialog(navController : NavHostController, showDialog : MutableState<Boolean>, heat : String, type : String, isLastItem : Boolean, onDismiss : () -> Unit) {
    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            showDialog.value = false
            navController.popBackStack()
        }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                Text(text="Item with heat $heat has been added to the $type.", Modifier.padding(16.dp))
                Button(onClick = {
                    showDialog.value = false
                    if (!isLastItem) {
                        navController.popBackStack(Screen.OptionsScreen.title, inclusive = false)
                    } else {
                        navController.navigate(Screen.ReviewScreen.title)
                    }
                    onDismiss()
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