package com.example.rusalqrandbarcodescanner.screens

import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@ExperimentalComposeUiApi
@Composable
fun LoadInfoInputScreen(navController: NavHostController, userInputViewModel: UserInputViewModel) {
    val focusManager = LocalFocusManager.current

    var loadVis by remember { mutableStateOf(userInputViewModel.loadVis.value) }
    val loadVisObserver = Observer<Boolean> { it ->
        loadVis = it
    }
    userInputViewModel.loadVis.observe(LocalLifecycleOwner.current, loadVisObserver)

    Scaffold(topBar = { TopAppBar(title = {Text(text = "Load Info", textAlign = TextAlign.Center) }) }) {

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {

            OrderInput(focusManager, userInputViewModel)
            LoadInput(focusManager, userInputViewModel)
            LoaderInput(focusManager, userInputViewModel)
            BlInput(focusManager, userInputViewModel)
            QuantityInput(focusManager, userInputViewModel)
            BundlesInput(focusManager, userInputViewModel)

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = { navController.popBackStack(Screen.MainMenuScreen.title, inclusive = false) }) {
                    Text(text = "Back", modifier = Modifier.padding(14.dp))
                }
                if (loadVis != null && loadVis!!) {
                    Button (onClick = { navController.navigate(Screen.ScannerScreen.title) }) {
                        Text(text = "Confirm Load Info", modifier = Modifier.padding(14.dp))
                    }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
fun OrderInput(focusManager: FocusManager, userInputViewModel: UserInputViewModel) {

    var order by remember { mutableStateOf(userInputViewModel.order.value) }
    val orderObserver = Observer<String> { it ->
        order = it
    }
    userInputViewModel.order.observe(LocalLifecycleOwner.current, orderObserver)
    order?.let{ ord ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
            value = ord,
            onValueChange = { it ->
                userInputViewModel.order.value = it
                userInputViewModel.refresh()
            } , label = { Text(text="Work Order: ") })
    }
}

@ExperimentalComposeUiApi
@Composable
fun LoadInput(focusManager: FocusManager, userInputViewModel: UserInputViewModel) {
    var load by remember { mutableStateOf(userInputViewModel.load.value) }
    val loadObserver = Observer<String> { it ->
        load = it
    }
    userInputViewModel.load.observe(LocalLifecycleOwner.current, loadObserver)
    load?.let{ loadIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
            value = loadIt,
            onValueChange = { it ->
                userInputViewModel.load.value = it
                userInputViewModel.refresh()
            } , label = { Text(text="Load Number: ") })
    }
}

@ExperimentalComposeUiApi
@Composable
fun LoaderInput(focusManager: FocusManager, userInputViewModel: UserInputViewModel) {
    var loader by remember { mutableStateOf(userInputViewModel.loader.value) }
    val loaderObserver = Observer<String> { it ->
        loader = it
    }
    userInputViewModel.loader.observe(LocalLifecycleOwner.current, loaderObserver)
    loader?.let{ loaderIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
            value = loaderIt,
            onValueChange = { it ->
                userInputViewModel.loader.value = it
                userInputViewModel.refresh()
            } , label = { Text(text="Loader: ") })
    }
}

@ExperimentalComposeUiApi
@Composable
fun BlInput(focusManager: FocusManager, userInputViewModel: UserInputViewModel) {
    var bl by remember { mutableStateOf(userInputViewModel.bl.value) }
    val blObserver = Observer<String> { it ->
        bl = it
    }
    userInputViewModel.bl.observe(LocalLifecycleOwner.current, blObserver)
    bl?.let{ blIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
            value = blIt,
            onValueChange = { it ->
                userInputViewModel.bl.value = it
                userInputViewModel.refresh()
            } , label = { Text(text="BL Number: ") })
    }
}

@ExperimentalComposeUiApi
@Composable
fun QuantityInput(focusManager: FocusManager, userInputViewModel: UserInputViewModel) {
    var quantity by remember { mutableStateOf(userInputViewModel.quantity.value) }
    val quantityObserver = Observer<String> { it ->
        quantity = it
    }
    userInputViewModel.quantity.observe(LocalLifecycleOwner.current, quantityObserver)
    quantity?.let{ quantityIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
            value = quantityIt,
            onValueChange = { it ->
                userInputViewModel.quantity.value = it
                userInputViewModel.refresh()
            } , label = { Text(text="Piece Count: ") })
    }
}

@ExperimentalComposeUiApi
@Composable
fun BundlesInput(focusManager: FocusManager, userInputViewModel: UserInputViewModel) {
    var bundles by remember { mutableStateOf(userInputViewModel.bundles.value) }
    val bundleObserver = Observer<String> { it ->
        bundles = it
    }
    userInputViewModel.bundles.observe(LocalLifecycleOwner.current, bundleObserver)
    bundles?.let{ bundlesIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true)}),
            value = bundlesIt,
            onValueChange = { it ->
                userInputViewModel.bundles.value = it
                userInputViewModel.refresh()
            } , label = { Text(text="Bundle Quantity: ") })
    }
}