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
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.database.RusalItem
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.presentation.components.StyledCardItem
import com.example.rusalqrandbarcodescanner.presentation.components.progress.SessionProgress
import com.example.rusalqrandbarcodescanner.util.Commodity
import com.example.rusalqrandbarcodescanner.util.stringInsertion
import com.example.rusalqrandbarcodescanner.util.getCommodity
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ReviewViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ReviewViewModel.ReviewViewModelFactory


@ExperimentalComposeUiApi
@Composable
fun ReviewScreen(mainActivityVM : MainActivityViewModel, onBack : () -> Unit, onConfirm : () -> Unit) {
    val application = LocalContext.current.applicationContext as CodeApplication

    val reviewVM : ReviewViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "ReviewVM", factory = ReviewViewModelFactory(application.invRepository, mainActivityVM))
    var item : RusalItem? by remember { mutableStateOf(null) }

    val sessionType = mainActivityVM.sessionType.value
    val displayRemoveEntry = mainActivityVM.displayRemoveEntryContent.value

    var showRemoveDialog by remember { mutableStateOf(false) }
    val showConfirmDialog = remember { mutableStateOf(false) }

    Scaffold(topBar = { TopAppBar(title = { Text(text="Review ${sessionType.type}", textAlign = TextAlign.Center) }) }) {

        SessionProgress(
            sessionType = sessionType,
            addedItems = if (sessionType == SessionType.SHIPMENT) mainActivityVM.addedItemCount.value else mainActivityVM.receivedItemCount.value,
            expectedItems =
            if (sessionType == SessionType.SHIPMENT)
                if (mainActivityVM.quantity.value == "") 0
                else mainActivityVM.quantity.value.toInt()
            else mainActivityVM.inboundItemCount.value,
            partiallyIdentifiedItems = 0,
            newItems = 0)

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
                        if (mainActivityVM.addedItemCount.value == 1) {
                            onBack()
                        }
                    },
                    item = item!!
                )
            }else {
                Text(text = if (displayRemoveEntry) {
                    "Please select the item you would like to remove or view additional information for:"
                } else {
                    "Review ${sessionType.type} - If you would like to remove or view additional information on an item please select it:"
                }, modifier = Modifier.padding(16.dp))

                GetRusalItemListView(
                    mainActivityVM.displayedItems.value,
                    onClick = {
                        item = it
                        showRemoveDialog = true
                    }
                )

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = onBack) {
                        Text(text = "Back", modifier = Modifier.padding(16.dp))
                    }
                    if (!displayRemoveEntry) {
                        Button(onClick = {
                            reviewVM.initiateUpdate()
                            reviewVM.clearAddedItems()
                            mainActivityVM.clearInputFields()
                            showConfirmDialog.value = true
                        }) {
                            Text(text = "Confirm ${sessionType.type}", modifier = Modifier.padding(16.dp))
                        }
                    }
                }
                if (showConfirmDialog.value) {
                    AlertDialog(
                        onDismissRequest = {
                           onConfirm()
                        }, title = { Text(text = "${sessionType.type} Confirmation") },
                        text = { Text(text = "${sessionType.type} Confirmed") },
                        buttons = {
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly) {
                                Button(onClick = onConfirm) { Text(text = "Ok", modifier = Modifier.padding(16.dp)) }
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
                Text(text = "Heat: ${ stringInsertion(item.heatNum, 6, "-") }" , style = MaterialTheme.typography.h6)
                Text(text="BL: ${item.blNum}", style = MaterialTheme.typography.h6)
                Text(if (getCommodity(item) == Commodity.BILLETS) {"Mark: ${item.mark}"} else {"Lot: ${item.lot}"}, style = MaterialTheme.typography.h6)
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
                StyledCardItem(text = "Heat Number: ${ stringInsertion(item.heatNum, 6, "-") }", backgroundColor = Color.Gray)
                StyledCardItem(text = "BL Number: ${item.blNum}", backgroundColor = Color.LightGray)
                StyledCardItem(text = "Mark: ${item.mark}", backgroundColor = Color.LightGray)
                if (getCommodity(item) == Commodity.INGOTS) {
                    StyledCardItem(text = "Lot: ${item.lot}", backgroundColor = Color.LightGray)
                }
                Text(text = "Piece Count: ${ item.quantity }")
                Text(text = "Net Weight Kg: ${ item.netWeightKg }")
                Text(text = "Gross Weight Kg: ${ item.grossWeightKg }")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onDismissRequest) {
                        Text("Dismiss", modifier = Modifier.padding(16.dp))
                    }
                    Button(onClick = {
                        onRemoveRequest()
                        onDismissRequest()
                    }) {
                        Text("Remove Item", modifier = Modifier.padding(16.dp))
                    }
                }
            }

        }
    }
}