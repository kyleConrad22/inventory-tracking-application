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
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ReturnedBundleViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ReturnedBundleViewModel.ReturnedBundleViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun ReturnedBundleScreen(navController: NavHostController, mainActivityVM : MainActivityViewModel) {
    val application  = LocalContext.current.applicationContext as CodeApplication

    val returnedBundleVM : ReturnedBundleViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "ReturnedBundleVM", factory = ReturnedBundleViewModelFactory(application.invRepository, mainActivityVM))

    val showAddedDialog = remember { mutableStateOf(false) }

    val heat = mainActivityVM.heatNum.value
    val loading = returnedBundleVM.loading.value
    val sessionType = mainActivityVM.sessionType.value
    val itemType = returnedBundleVM.itemActionType.value
    val locatedItem = returnedBundleVM.locatedItem.value

    if (showAddedDialog.value) {
        BundleAddedDialog(navController, showAddedDialog, heat, mainActivityVM.sessionType.value.type, returnedBundleVM.isLastBundle(sessionType))
    } else {
        Scaffold(topBar = { TopAppBar(title = { Text("Returned Bundle Info") }) }) {

            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                if (loading) {
                    LoadingDialog(isDisplayed = true)
                } else {
                    when (itemType) {
                        ItemActionType.MULTIPLE_BLS_OR_PIECE_COUNTS -> MultipleBlsOrPieceCounts(uniqueComboList = returnedBundleVM.uniqueList.value, heat = heat, onDismiss = { handleDismiss(navController, mainActivityVM.heatNum) }, onConfirm = {
                            returnedBundleVM.addItem()
                            showAddedDialog.value = true
                        })
                        ItemActionType.NOT_IN_LOADED_HEATS -> NotInLoadedHeats(loadedHeatList = returnedBundleVM.getLoadedHeats(), heat = heat, onDismiss = { handleDismiss(navController, mainActivityVM.heatNum) })
                        ItemActionType.INCORRECT_PIECE_COUNT -> IncorrectField(field = "Piece Count", retrievedValue = locatedItem!!.quantity, requestedValue = mainActivityVM.pieceCount.value, heat = heat, onDismiss = { handleDismiss(navController, mainActivityVM.heatNum) })
                        ItemActionType.INCORRECT_BL -> IncorrectField(field = "BL Number", retrievedValue = locatedItem!!.blNum, requestedValue = mainActivityVM.bl.value, heat = heat, onDismiss = { handleDismiss(navController, mainActivityVM.heatNum) })
                        ItemActionType.DUPLICATE -> DuplicateItem(sessionType = sessionType.type, scanTime = locatedItem!!.loadTime, heat = heat, onDismiss = { handleDismiss(navController, mainActivityVM.heatNum) })
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
                            returnedBundleVM.addItem()
                            showAddedDialog.value = true
                        })
                    }
                }
            }
        }
    }
}

/*TODO - Allow user to manually enter data / scan data to retrieve bundle information*/
fun handleAddition() {

}

fun handleDismiss(navController: NavHostController, heat : MutableState<String>) {
    heat.value = ""
    navController.popBackStack(Screen.OptionsScreen.title, inclusive = true)
}

@Composable
private fun IncorrectField(field: String, heat: String, retrievedValue: String, requestedValue: String, onDismiss : () -> Unit) {
    Text(text =
    """
        Item with heat $heat cannot be added to shipment!
        Incorrect $field, returned item has a $field of $retrievedValue and the requested $field is $requestedValue.
    """.trimIndent(), modifier = Modifier.padding(16.dp))
    DismissButton {
        onDismiss()
    }
}

@Composable
private fun DuplicateItem(sessionType : String, scanTime : String, heat : String, onDismiss: () -> Unit) {
    Text(text="""
        Item with heat $heat cannot be added to $sessionType!
        Item was already added at $scanTime.
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
    Text(text =
    """
        Heat Number: ${item.heatNum}
        Piece Count: ${item.quantity}
        BL Number: ${item.blNum}
        Mark: ${item.mark}
    """.trimIndent())
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
private fun BundleAddedDialog(navController : NavHostController, showDialog : MutableState<Boolean>, heat : String, type : String, isLastBundle : Boolean) {
    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            showDialog.value = false
            navController.popBackStack()
        }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                Text(text="Bundle $heat, has been added to the $type.", Modifier.padding(16.dp))
                Button(onClick = {
                    showDialog.value = false
                    if (!isLastBundle) {
                        navController.popBackStack(Screen.OptionsScreen.title, inclusive = true)
                    } else {
                        navController.navigate(Screen.ReviewScreen.title)
                    }
                }) {
                    Text(if (!isLastBundle) {"Ok"} else { "Review Load" }, modifier = Modifier.padding(16.dp))
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