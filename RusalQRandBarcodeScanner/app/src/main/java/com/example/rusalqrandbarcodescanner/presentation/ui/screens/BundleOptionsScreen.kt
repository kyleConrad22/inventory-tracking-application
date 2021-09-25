package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.util.ReturnedBundleOptions
import com.example.rusalqrandbarcodescanner.viewModels.BundleOptionsViewModel
import com.example.rusalqrandbarcodescanner.viewModels.BundleOptionsViewModel.BundleOptionsViewModelFactory

@Composable
fun BundleOptionsScreen(navController : NavController, options : ReturnedBundleOptions) {
    val application = LocalContext.current.applicationContext
    val bundleOptionsViewModel : BundleOptionsViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "bundleOptionsVM", BundleOptionsViewModelFactory((application as CodeApplication).repository, application.invRepository))

    Scaffold(topBar = { TopAppBar(title = { Text("Bundle Options") }) }) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text="Heat has the following BLs and quantities; please confirm the bundle's quantity is ${options.userQuantity}and BL is ${options.userBl}:")
            OptionsList(options = options.options)
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    /*TODO - Add Deny Logic to Remove from Inventory and Scanned Bundles*/
                    navController.popBackStack()
                }) {
                    Text(text="Deny", modifier = Modifier.padding(16.dp))
                }
                Button(onClick = { navController.navigate(Screen.ScannerScreen.title) }) {
                    Text(text="Confirm Addition", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
private fun OptionsList(options : List<List<String>>) {
    LazyColumn(modifier = Modifier
        .background(Color.LightGray)
        .size(400.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        items(
            items = options,
            itemContent = {
                OptionsListItem(option = it)
            }
        )
    }
}

@Composable
private fun OptionsListItem(option : List<String>) {
    Card(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp).fillMaxWidth(),
        elevation = 2.dp, backgroundColor = Color.Black, shape = RoundedCornerShape(corner = CornerSize(16.dp))) {
        Row {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth().align(Alignment.CenterVertically)) {
                Text(text="BL: ${option[0]} || Quantity: ${option[1]}", style = MaterialTheme.typography.h6)
            }
        }
    }
}