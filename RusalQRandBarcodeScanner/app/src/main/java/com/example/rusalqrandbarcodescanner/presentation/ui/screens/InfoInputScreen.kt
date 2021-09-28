package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import android.util.Log
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
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
import com.example.rusalqrandbarcodescanner.domain.models.Bl
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.presentation.components.autocomplete.AutoCompleteBox
import com.example.rusalqrandbarcodescanner.viewmodels.InfoInputViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@ExperimentalAnimationApi
@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun InfoInputScreen(navController: NavHostController) {
    val application = LocalContext.current.applicationContext
    val infoInputViewModel: InfoInputViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "infoInputVM", factory = InfoInputViewModel.InfoInputViewModelFactory((application as CodeApplication).userRepository, application.invRepository))

    val focusManager = LocalFocusManager.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var isLoad by remember { mutableStateOf(infoInputViewModel.isLoad().value) }
    val isLoadObserver = Observer<Boolean> { it ->
        isLoad = it
    }
    infoInputViewModel.isLoad().observe(lifecycleOwner, isLoadObserver)

    var confirmVis by remember { mutableStateOf(infoInputViewModel.confirmVis.value) }
    val confirmVisObserver = Observer<Boolean> {
        confirmVis = it
    }
    infoInputViewModel.confirmVis.observe(lifecycleOwner, confirmVisObserver)

    val loading = infoInputViewModel.loading.value
    val isClicked = remember { mutableStateOf(false) }

    if (isLoad == null || loading) {
        CircularIndeterminateProgressBar(isDisplayed = loading)
    } else {
        val type = if (isLoad != null) {
            if (isLoad!!) { "Input" } else { "Reception" }
        } else { "" }

        Scaffold(topBar = { TopAppBar(title = { Text(text = "$type Info Input", textAlign = TextAlign.Center) }) }) {

            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {

                if (loading || isClicked.value) {
                    Log.d("DEBUG", loading.toString())
                    LoadingDialog(isDisplayed = true)

                } else {
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
                            if (!isClicked.value) {
                                navController.popBackStack(Screen.MainMenuScreen.title,
                                    inclusive = false)
                            }
                        }) {
                            Text(text = "Back", modifier = Modifier.padding(14.dp))
                        }
                        if (confirmVis != null && confirmVis!!) {
                            Button(onClick = {
                                if (!isClicked.value) {
                                    infoInputViewModel.getUpdate()
                                    isClicked.value = true
                                }
                            }) {
                                Text(text = "Confirm $type Info",
                                    modifier = Modifier.padding(14.dp))
                            }
                        }
                    }
                }
                if (!loading && isClicked.value) {
                    navController.navigate(Screen.OptionsScreen.title)
                    isClicked.value = false
                }
            }
        }
    }

}

@ExperimentalComposeUiApi
@DelicateCoroutinesApi
@Composable
fun OrderInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    var order by remember { mutableStateOf(infoInputViewModel.order.value) }
    val orderObserver = Observer<String> { it ->
        order = it
    }
    infoInputViewModel.order.observe(LocalLifecycleOwner.current, orderObserver)


    order?.let{ ord ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password, capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
            value = ord,
            onValueChange = { it ->
                infoInputViewModel.order.value = it
                infoInputViewModel.refresh()
            } , label = { Text(text="Work Order: ") })
    }
}

@DelicateCoroutinesApi
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

@DelicateCoroutinesApi
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

@DelicateCoroutinesApi
@ExperimentalAnimationApi
@Composable
fun BlInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    val blList = infoInputViewModel.blList.value
    Log.d("DEBUG", if (blList.isEmpty()) {"Empty!"} else {blList[0].blNumber})
    AutoCompleteBox(
        items = blList,
        itemContent = { bl ->
            BlAutoCompleteItem(bl)
        }
    ) {

        val value = infoInputViewModel.bl.value

        onItemSelected { bl ->
            infoInputViewModel.bl.value = bl.blNumber
            filter(bl.blNumber)
            focusManager.moveFocus(FocusDirection.Down)
        }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(.9f).onFocusChanged { focusState ->
                isSearching = focusState.isFocused
                println(isSearching)
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password, capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            value = value,
            onValueChange = {  it ->
                infoInputViewModel.bl.value = it
                filter(it)
                infoInputViewModel.refresh()
            },
            label = { Text(text="BL Number: ") })
    }
}

@Composable
fun BlAutoCompleteItem(bl : Bl) {
    Log.d("DEBUG", bl.blNumber)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text=bl.blNumber, style = MaterialTheme.typography.subtitle2)
    }
}

/*
@DelicateCoroutinesApi
@Composable
fun BlInput(focusManager: FocusManager, infoInputViewModel: InfoInputViewModel) {
    var bl by remember { mutableStateOf(infoInputViewModel.bl.value) }
    val blObserver = Observer<String> { it ->
        bl = it
    }
    infoInputViewModel.bl.observe(LocalLifecycleOwner.current, blObserver)

    bl?.let{ blIt ->
        OutlinedTextField(singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password, capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down)}),
            value = blIt,
            onValueChange = { it ->
                infoInputViewModel.bl.value = it
                infoInputViewModel.refresh()
            } , label = { Text(text="BL Number: ") })
    }
}
 */

@DelicateCoroutinesApi
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

@DelicateCoroutinesApi
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

@DelicateCoroutinesApi
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

@DelicateCoroutinesApi
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