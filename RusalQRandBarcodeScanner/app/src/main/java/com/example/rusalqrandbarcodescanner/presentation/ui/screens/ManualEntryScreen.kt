package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.viewmodels.ManualEntryViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.ManualEntryViewModel.ManualEntryViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun ManualEntryScreen(navController : NavHostController) {

    val focusManager = LocalFocusManager.current
    val manualEntryViewModel : ManualEntryViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "manualEntryVM", ManualEntryViewModelFactory((LocalContext.current.applicationContext as CodeApplication).userRepository))

    val isSearchVis = manualEntryViewModel.isSearchVis.value

    val loading = manualEntryViewModel.loading.value
    val isClicked = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        if (loading || isClicked.value) {
            LoadingDialog(isDisplayed = true)
        } else {
            Text(text = "Manual Heat Number Search: ", modifier = Modifier.padding(16.dp))
            HeatNumberInput(focusManager, manualEntryViewModel)
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    navController.navigateUp()
                }) {
                    Text(text = "Back", modifier = Modifier.padding(16.dp))
                }
                if (isSearchVis) {
                    Button(onClick = {
                        manualEntryViewModel.updateHeat()
                        isClicked.value = true
                    }) {
                        Text(text = "Retrieve Bundle Info", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
    if (!loading && isClicked.value) {
        navController.navigate(Screen.ReturnedBundleScreen.title)
        isClicked.value = false
    }
}

@DelicateCoroutinesApi
@Composable
private fun HeatNumberInput(focusManager : FocusManager, manualEntryViewModel : ManualEntryViewModel) {
    val heat = manualEntryViewModel.heat.value

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(.9f),
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
        keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus(true) }),
        value = heat,
        onValueChange = { it ->
            manualEntryViewModel.heat.value = it
            manualEntryViewModel.refresh() },
        label = { Text(text = "Heat Number: ") })
}