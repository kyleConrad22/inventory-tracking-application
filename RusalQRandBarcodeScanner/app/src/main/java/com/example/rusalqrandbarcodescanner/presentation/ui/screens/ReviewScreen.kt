package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ReviewViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ReviewViewModel.ReviewViewModelFactory


@ExperimentalComposeUiApi
@Composable
fun ReviewScreen(navController: NavHostController, mainActivityVM : MainActivityViewModel) {
    val application = LocalContext.current.applicationContext as CodeApplication

    val reviewVM : ReviewViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "ReviewVM", factory = ReviewViewModelFactory(application.invRepository, mainActivityVM))
    var item : RusalItem? by remember { mutableStateOf(null) }

    val sessionType = mainActivityVM.sessionType.value
    val displayRemoveEntry = mainActivityVM.displayRemoveEntryContent.value

    var showRemoveDialog by remember { mutableStateOf(false) }
    val showConfirmDialog = remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text(text="Rusal Scanner", textAlign = TextAlign.Center) }) }) {

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            if (showRemoveDialog) {
                RemoveDialog(onDismissRequest = { showRemoveDialog = false },
                    onRemoveRequest = {
                        reviewVM.removeItem(item!!)
                        mainActivityVM.refresh()
                    },
                    item = item!!)
            }else {
                Text(text = if (displayRemoveEntry) {
                    "Please select the bundle you would like to remove:"
                } else {
                    "Review ${sessionType.type}; if you would like to remove a bundle please select it:"
                }, modifier = Modifier.padding(16.dp))

                GetRusalItemListView(
                    mainActivityVM.addedItems.value,
                    onClick = {
                        item = it
                        showRemoveDialog = true
                    }
                )

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        navController.popBackStack(Screen.OptionsScreen.title, inclusive = false)
                    }) {
                        Text(text = "Back", modifier = Modifier.padding(16.dp))
                    }
                    if (!displayRemoveEntry) {
                        Button(onClick = {
                            if (sessionType == SessionType.SHIPMENT) {
                                reviewVM.initiateUpdate()
                            } else {
                                /*TODO - Add Reception Confirmation Logic */
                            }
                            reviewVM.removeAllAddedItems()
                            mainActivityVM.refresh()
                            showConfirmDialog.value = true
                        }) {
                            Text(text = "Confirm ${sessionType.type}", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                if (showConfirmDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                            navController.popBackStack(Screen.MainMenuScreen.title,
                                inclusive = true)
                        },
                        title = { Text(text = "${sessionType.type} Confirmation") },
                        text = { Text(text = "${sessionType.type} Confirmed") },
                        buttons = {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly) {
                                Button(onClick = {
                                    navController.popBackStack(Screen.MainMenuScreen.title,
                                        inclusive = true)
                                }) { Text(text = "Ok", modifier = Modifier.padding(16.dp)) }
                            }
                        }
                    )
                }
            }
        }
    }
}



@Composable
private fun GetRusalItemListView(items : List<RusalItem>, onClick : (item : RusalItem) -> Unit) {

    LazyColumn (
        modifier = Modifier
            .background(Color.LightGray)
            .size(400.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(
            items = items,
            itemContent = { it ->
                RusalItemListItem(item = it, onClick = { onClick(it) })
            }
        )
    }
}

@Composable
private fun RusalItemListItem(item: RusalItem, onClick: (item : RusalItem) -> Unit) {
    Card(
        modifier= Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable(onClick = { onClick(item) })
            .fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = Color.White,
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Row {
            Column(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.CenterVertically)) {
                Text(text = "Heat: ${item.heatNum}" , style = MaterialTheme.typography.h6)
                Text(text="BL: ${item.blNum}", style = MaterialTheme.typography.h6)
                Text("Mark: ${item.mark}", style = MaterialTheme.typography.h6)
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun RemoveDialog(onDismissRequest : () -> Unit, onRemoveRequest : () -> Unit, item : RusalItem) {
    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                Text(text = "Heat Number: ${ item.heatNum }")
                Text(text = "BL Number: ${ item.blNum }")
                Text(text = "Quantity: ${ item.quantity }")
                Text(text = "Net Weight Kg: ${ item.netWeightKg }")
                Text(text = "Gross Weight Kg: ${ item.grossWeightKg }")
                Text(text = "Mark: ${item.mark}")
                Text(text = "Barcode: ${item.barcode}")
                Button(onClick = onDismissRequest) {
                    Text("Deny Removal", modifier = Modifier.padding(16.dp))
                }
                Button(onClick = {
                    onRemoveRequest()
                    onDismissRequest()
                }) {
                    Text("Remove Bundle", modifier = Modifier.padding(16.dp))
                }
            }

        }
    }
}