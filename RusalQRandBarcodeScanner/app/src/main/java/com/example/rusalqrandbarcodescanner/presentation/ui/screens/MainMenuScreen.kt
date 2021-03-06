package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.domain.models.SessionType
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@ExperimentalComposeUiApi
@DelicateCoroutinesApi
@Composable
fun MainMenuScreen(mainActivityVM: MainActivityViewModel, onNavRequest : (dest : String) -> Unit) {

    val newSessionType = remember { mutableStateOf(mainActivityVM.sessionType.value) }
    val showAlertDialog = remember { mutableStateOf(false) }
    val hasItems = mainActivityVM.addedItemCount.value > 0

    Scaffold(topBar = { TopAppBar(title = { Text(text="Main Menu", textAlign = TextAlign.Center) }) }) {


        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {

            Button(onClick = {
                newSessionType.value = SessionType.SHIPMENT
                handleClick(onNavRequest = onNavRequest, mainActivityVM.sessionType, SessionType.SHIPMENT, hasItems, showAlertDialog, onDiffentSessionTypes = { mainActivityVM.clearInputFields() })
            }) {
                Text(text = "New Shipment",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
            Button(onClick = {
                newSessionType.value = SessionType.RECEPTION
                handleClick(onNavRequest = onNavRequest, mainActivityVM.sessionType, SessionType.RECEPTION, hasItems, showAlertDialog, onDiffentSessionTypes = { mainActivityVM.clearInputFields() })
            }) {
                Text(text = "New Reception",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
            Button(onClick = {
                newSessionType.value = SessionType.GENERAL
                handleClick(onNavRequest = onNavRequest, mainActivityVM.sessionType, SessionType.GENERAL, hasItems, showAlertDialog,onDiffentSessionTypes = { mainActivityVM.clearInputFields() })
            }) {
                Text(text = "Get Item Info",
                    modifier = Modifier
                        .padding(16.dp)
                        .size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
        }
    }
    if (showAlertDialog.value) {
        NavigationAlertDialog(
            onDismiss = {
                showAlertDialog.value = false
            }, onConfirm = {
                mainActivityVM.removeAllAddedItems()
                mainActivityVM.clearInputFields()
                handleClick(onNavRequest = onNavRequest, mainActivityVM.sessionType, newSessionType.value, containsItems = false, showAlertDialog, onDiffentSessionTypes =  { mainActivityVM.clearInputFields() })
            }, onReview = {
                onNavRequest(Screen.ReviewScreen.title)
            }, currentSessionType = mainActivityVM.sessionType.value, newSessionType = newSessionType.value
        )
    }
}

fun handleClick(onNavRequest: (dest: String) -> Unit, currentSessionType: MutableState<SessionType>, newSessionType: SessionType, containsItems: Boolean, showAlertDialog : MutableState<Boolean>, onDiffentSessionTypes : () -> Unit) {
    if (!containsItems || currentSessionType.value == newSessionType) {

        if (currentSessionType.value != newSessionType) onDiffentSessionTypes()

        currentSessionType.value = newSessionType

        onNavRequest(
            if (newSessionType != SessionType.GENERAL) Screen.InfoInputScreen.title
            else Screen.ToBeImplementedScreen.title
        )

    } else {
        showAlertDialog.value = true
    }
}

@ExperimentalComposeUiApi
@Composable
fun NavigationAlertDialog(onDismiss : () -> Unit, onConfirm: () -> Unit, onReview : () -> Unit, currentSessionType: SessionType, newSessionType: SessionType) {
    Dialog(properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = {
            onDismiss()
        }) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
                Text(text = "Navigation Confirmation", modifier = Modifier.padding(16.dp) )
                Text(text = """
                    You have already added items to a ${currentSessionType.type}, starting another session will delete all saved items from current session. 
                    
                    Are you sure you would like to navigate start a new ${newSessionType.type}?
                    """.trimIndent()
                , modifier = Modifier.padding(16.dp))
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        onReview()
                    }) {
                        Text("Review ${currentSessionType.type}",
                            modifier = Modifier.padding(16.dp))
                    }
                    Button(onClick = {
                        onConfirm()
                    }) {
                        Text(text = "Continue to ${newSessionType.type}",
                            modifier = Modifier.padding(16.dp))
                    }
                }
                Button(onClick = {
                    onDismiss()
                }) {
                    Text(text = "Dismiss", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}