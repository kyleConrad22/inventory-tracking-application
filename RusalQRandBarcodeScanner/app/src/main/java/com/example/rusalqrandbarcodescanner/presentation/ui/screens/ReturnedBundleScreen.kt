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
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ReturnedBundleViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.ReturnedBundleViewModel.ReturnedBundleViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun ReturnedBundleScreen(navController: NavHostController) {
    val application  = LocalContext.current.applicationContext as CodeApplication
    val returnedBundleVM : ReturnedBundleViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "ReturnedBundleVM", factory = ReturnedBundleViewModelFactory(application.invRepository, application.userRepository))

    val showAddedDialog = remember { mutableStateOf(false) }

    val loading = returnedBundleVM.loading.value

    if (showAddedDialog.value) {
        BundleAddedDialog(navController, showAddedDialog, returnedBundleVM.getHeat(), returnedBundleVM.getType(), returnedBundleVM.isLastBundle())
    } else {
        Scaffold(topBar = { TopAppBar(title = { Text("Returned Bundle Info") }) }) {

            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                if (loading) {
                    LoadingDialog(isDisplayed = true)
                } else {
                    Text(text = returnedBundleVM.reasoning, modifier = Modifier.padding(16.dp))
                    if (returnedBundleVM.isIncorrectBundle) {
                        Button(onClick = {
                            navController.popBackStack()
                        }) {
                            Text(text = "Ok", modifier = Modifier.padding(16.dp))
                        }
                    } else {
                        if (returnedBundleVM.isMultipleOptions) {
                            UniqueOptionsList(returnedBundleVM.uniqueList)
                        }
                        Row(modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly) {
                            Button(onClick = {
                                navController.popBackStack()
                            }) {
                                Text(text = "Deny", modifier = Modifier.padding(16.dp))
                            }
                            Button(onClick = {
                                returnedBundleVM.addBundle()
                                showAddedDialog.value = true
                            }) {
                                Text(text = "Add", modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                }
            }
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
                        navController.popBackStack()
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
private fun UniqueOptionsList(uniqueItems : List<List<String>>) {
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
private fun UniqueOption(item : List<String>) {
    Card(modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 8.dp)
        .fillMaxWidth(),
        elevation = 2.dp, backgroundColor = Color.White, shape = RoundedCornerShape(corner = CornerSize(16.dp))) {
        Row {
            Column (modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.CenterVertically)) {
                Text(text="BL: ${item[0]}\nQuantity: ${item[1]}", style = MaterialTheme.typography.h6)
            }
        }
    }
}