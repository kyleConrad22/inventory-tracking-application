package com.example.rusalqrandbarcodescanner.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@ExperimentalComposeUiApi
@Composable
fun ReceptionInfoInputScreen(navController: NavHostController, userInputViewModel: UserInputViewModel) {
    val focusManager = LocalFocusManager.current
    var receptionVis by remember { mutableStateOf(userInputViewModel.receptionVis.value) }
    val receptionVisObserver = Observer<Boolean> { it ->
        receptionVis = it
    }
    userInputViewModel.receptionVis.observe(LocalLifecycleOwner.current, receptionVisObserver)

    Scaffold(topBar = { TopAppBar(title = { Text(text = "Reception Info", textAlign = TextAlign.Center) }) }) {
        Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            vesselInput(focusManager = focusManager, userInputViewModel = userInputViewModel)
            checkerInput(focusManager = focusManager, userInputViewModel = userInputViewModel)
            Button(onClick = { navController.popBackStack(Screen.MainMenuScreen.title, inclusive = false) }) {
                Text(text = "Back", modifier = Modifier.padding(16.dp))
            }
            if (receptionVis != null && receptionVis!!) {
                Button(onClick = { navController.navigate(Screen.ReceptionOptionsScreen.title) }) {
                    Text(text = "Confirm Reception Info", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun vesselInput(focusManager: FocusManager, userInputViewModel: UserInputViewModel) {
    var vessel by remember { mutableStateOf(userInputViewModel.vessel.value) }
    val vesselObserver = Observer<String> { it ->
        vessel = it
    }
    userInputViewModel.vessel.observe(LocalLifecycleOwner.current, vesselObserver)
    vessel?.let{ vesselIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            value = vesselIt,
            onValueChange = { it ->
                userInputViewModel.vessel.value = it
                userInputViewModel.refresh()
            }, label = { Text(text = "Vessel:") })
    }
}

@ExperimentalComposeUiApi
@Composable
fun checkerInput(focusManager: FocusManager, userInputViewModel: UserInputViewModel) {
    var checker by remember { mutableStateOf(userInputViewModel.checker.value) }
    val checkerObserver =Observer<String> { it ->
        checker = it
    }
    userInputViewModel.checker.observe(LocalLifecycleOwner.current, checkerObserver)
    checker?.let { checkerIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(KeyboardCapitalization.Characters, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) }),
            value = checkerIt,
            onValueChange = { it ->
                userInputViewModel.checker.value = it
                userInputViewModel.refresh()
            }, label = { Text("Checker:") })
    }
}