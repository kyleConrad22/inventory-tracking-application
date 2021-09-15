package com.example.rusalqrandbarcodescanner.presentation.ui.screens

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CircularIndeterminateProgressBar
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewModels.InfoInputViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@ExperimentalComposeUiApi
@Composable
fun InfoInputScreen(navController: NavHostController, userInputViewModel: UserInputViewModel) {
    val infoInputViewModel: InfoInputViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "infoInputVM", factory = InfoInputViewModel.InfoInputViewModelFactory((LocalContext.current.applicationContext as CodeApplication).userRepository))

    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isLoad by remember { mutableStateOf(infoInputViewModel.isLoad().value) }
    val isLoadObserver = Observer<Boolean> { it ->
        isLoad = it
    }
    infoInputViewModel.isLoad().observe(lifecycleOwner, isLoadObserver)

    var confirmVis by remember { mutableStateOf(infoInputViewModel.confirmVis.value) }
    val confirmVisObserver = Observer<Boolean> { it ->
        confirmVis = it
    }
    infoInputViewModel.confirmVis.observe(lifecycleOwner, confirmVisObserver)

    val loading = infoInputViewModel.loading.value

    if (isLoad == null || loading) {
        CircularIndeterminateProgressBar(isDisplayed = loading)
    } else {
        val type = if (isLoad != null) {
            if (isLoad!!) { "Input" } else { "Reception" }
        } else { "" }

        Scaffold(topBar = {
            TopAppBar(title = {
                Text(text = "$type Info Input",
                    textAlign = TextAlign.Center)
            })
        }) {
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {

                if (isLoad != null) {
                    if (isLoad!!) {
                        OrderInput(focusManager, infoInputViewModel)
                        LoadInput(focusManager, infoInputViewModel)
                        LoaderInput(focusManager, infoInputViewModel)
                        BlInput(focusManager, infoInputViewModel)
                        QuantityInput(focusManager, infoInputViewModel)
                        BundlesInput(focusManager, infoInputViewModel)
                    } else {
                        VesselInput(focusManager, infoInputViewModel)
                        CheckerInput(focusManager, infoInputViewModel)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        navController.popBackStack(Screen.MainMenuScreen.title,
                            inclusive = false)
                    }) {
                        Text(text = "Back", modifier = Modifier.padding(14.dp))
                    }
                    if (confirmVis != null && confirmVis!!) {
                        Button(onClick = {
                            infoInputViewModel.update()
                            navController.navigate(Screen.OptionsScreen.title)
                        }) {
                            Text(text = "Confirm $type Info", modifier = Modifier.padding(14.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    var order by remember { mutableStateOf(infoInputViewModel.order.value) }
    val orderObserver = Observer<String> { it ->
        order = it
    }
    infoInputViewModel.order.observe(LocalLifecycleOwner.current, orderObserver)

    order?.let{ ord ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
            value = ord,
            onValueChange = { it ->
                infoInputViewModel.order.value = it
                infoInputViewModel.refresh()
            } , label = { Text(text="Work Order: ") })
    }
}

@Composable
fun LoadInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    var load by remember { mutableStateOf(infoInputViewModel.load.value) }
    val loadObserver = Observer<String> { it ->
        load = it
    }
    infoInputViewModel.load.observe(LocalLifecycleOwner.current, loadObserver)

    load?.let{ loadIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
            value = loadIt,
            onValueChange = { it ->
                infoInputViewModel.load.value = it
                infoInputViewModel.refresh()
            } , label = { Text(text="Load Number: ") })
    }
}

@Composable
fun LoaderInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    var loader by remember { mutableStateOf(infoInputViewModel.loader.value) }
    val loaderObserver = Observer<String> { it ->
        loader = it
    }
    infoInputViewModel.loader.observe(LocalLifecycleOwner.current, loaderObserver)

    loader?.let{ loaderIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
            value = loaderIt,
            onValueChange = { it ->
                infoInputViewModel.loader.value = it
                infoInputViewModel.refresh()
            } , label = { Text(text="Loader: ") })
    }
}

@Composable
fun BlInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    var bl by remember { mutableStateOf(infoInputViewModel.bl.value) }
    val blObserver = Observer<String> { it ->
        bl = it
    }
    infoInputViewModel.bl.observe(LocalLifecycleOwner.current, blObserver)

    bl?.let{ blIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
            value = blIt,
            onValueChange = { it ->
                infoInputViewModel.bl.value = it
                infoInputViewModel.refresh()
            } , label = { Text(text="BL Number: ") })
    }
}

@Composable
fun QuantityInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    var quantity by remember { mutableStateOf(infoInputViewModel.quantity.value) }
    val quantityObserver = Observer<String> { it ->
        quantity = it
    }
    infoInputViewModel.quantity.observe(LocalLifecycleOwner.current, quantityObserver)

    quantity?.let{ quantityIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
            value = quantityIt,
            onValueChange = { it ->
                infoInputViewModel.quantity.value = it
                infoInputViewModel.refresh()
            } , label = { Text(text="Piece Count: ") })
    }
}

@Composable
fun BundlesInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    var bundles by remember { mutableStateOf(infoInputViewModel.bundles.value) }
    val bundleObserver = Observer<String> { it ->
        bundles = it
    }
    infoInputViewModel.bundles.observe(LocalLifecycleOwner.current, bundleObserver)

    bundles?.let{ bundlesIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true)}),
            value = bundlesIt,
            onValueChange = { it ->
                infoInputViewModel.bundles.value = it
                infoInputViewModel.refresh()
            } , label = { Text(text="Bundle Quantity: ") })
    }
}

@Composable
fun VesselInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    var vessel by remember { mutableStateOf(infoInputViewModel.vessel.value) }
    val vesselObserver = Observer<String> { it ->
        vessel = it
    }
    infoInputViewModel.vessel.observe(LocalLifecycleOwner.current, vesselObserver)

    vessel?.let{ vesselIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            value = vesselIt,
            onValueChange = { it ->
                infoInputViewModel.vessel.value = it
                infoInputViewModel.refresh()
            }, label = { Text(text = "Vessel:") })
    }
}

@Composable
fun CheckerInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    var checker by remember { mutableStateOf(infoInputViewModel.checker.value) }
    val checkerObserver = Observer<String> { it ->
        checker = it
    }
    infoInputViewModel.checker.observe(LocalLifecycleOwner.current, checkerObserver)

    checker?.let { checkerIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(KeyboardCapitalization.Characters, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus(true) }),
            value = checkerIt,
            onValueChange = { it ->
                infoInputViewModel.checker.value = it
                infoInputViewModel.refresh()
            }, label = { Text("Checker:") })
    }
}