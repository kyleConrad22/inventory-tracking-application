package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.presentation.components.CircularIndeterminateProgressBar
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewmodels.*

@Composable
fun OptionsScreen(navController: NavHostController) {
    val application = LocalContext.current.applicationContext

    val optionsViewModel : OptionsViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "optionsVM", factory = OptionsViewModel.OptionsViewModelFactory((application as CodeApplication).userRepository, (application as CodeApplication).repository))

    val loading = optionsViewModel.loading.value
    val displayCountButtons = optionsViewModel.displayCountButtons.value

    val resetDialog = remember { mutableStateOf(false) }

    var isLoad by remember { mutableStateOf(optionsViewModel.isLoad().value) }
    val isLoadObserver = Observer<Boolean> { it ->
        isLoad = it
    }
    optionsViewModel.isLoad().observe(LocalLifecycleOwner.current, isLoadObserver)

    val displayRemoveEntry = optionsViewModel.displayRemoveEntry.value

    if (isLoad == null) {
        CircularIndeterminateProgressBar(isDisplayed = loading)
    } else {
        val input = optionsViewModel.currentInput.value!![0]

        val type = if (isLoad!!) { "Load" } else { "Reception" }
        Scaffold(topBar = { TopAppBar(title = { Text("$type Options", textAlign = TextAlign.Center) }) }) {

            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                Text(text = "$type Options:")
                if (isLoad!!) {
                    Text(text = "${input.order!!} Load ${input.load!!}")
                } else {
                    Text(text = "Vessel Project: ${input.vessel!!}")
                }
                Button(onClick = { navController.navigate(Screen.ScannerScreen.title) }) {
                    Text(text = "Scan Code", modifier = Modifier
                        .padding(16.dp)
                        .size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically), textAlign = TextAlign.Center)
                }
                if (displayCountButtons) {
                    if (isLoad!!) {
                        Button(onClick = {
                            resetDialog.value = true
                        }) {
                            Text(text = "Reset Load", modifier = Modifier
                                .padding(16.dp)
                                .size(width = 200.dp, height = 20.dp)
                                .align(Alignment.CenterVertically), textAlign = TextAlign.Center)
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        navController.navigate(Screen.InfoInputScreen.title)
                    }) {
                        Text(text = "Back", modifier = Modifier.padding(16.dp))
                    }
                    if (displayCountButtons) {
                        Button(onClick = {
                            navController.navigate(Screen.ReviewScreen.title)
                        }) {
                            Text(text = if (displayRemoveEntry) {
                                "RemoveEntry"
                            } else {
                                "Review $type"
                            }, modifier = Modifier.padding(16.dp))
                        }
                    }
                    if (resetDialog.value) {
                        AlertDialog(
                            onDismissRequest = { resetDialog.value = false },
                            title = { Text(text = "Reset $type Confirmation") },
                            text = { Text(text = "Are you sure you would like to remove all bundles from this Load? This cannot be undone.") },
                            buttons = {
                                Row(modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly) {
                                    Button(onClick = {
                                        resetDialog.value = false
                                    }) {
                                        Text(text = "Deny Reset",
                                            modifier = Modifier.padding(16.dp))
                                    }
                                    Button(onClick = {
                                        resetDialog.value = false
                                        optionsViewModel.deleteAll()
                                    }) {
                                        Text(text = "Confirm Reset",
                                            modifier = Modifier.padding(16.dp))
                                    }
                                }
                            })
                    }
                }
            }
        }
    }
}
