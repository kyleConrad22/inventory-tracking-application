package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.ScannedInfo
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.viewModels.BundleAddedViewModel
import com.example.rusalqrandbarcodescanner.viewModels.BundleAddedViewModel.BundleAddedViewModelFactory

@Composable
fun BundleAddedScreen(navController: NavHostController) {
    val application  = LocalContext.current.applicationContext
    val bundleAddedViewModel : BundleAddedViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "bundleAddedViewModel", factory = BundleAddedViewModelFactory((application as CodeApplication).userRepository, application.repository))

    val loading = bundleAddedViewModel.loading.value
    val isLoad = bundleAddedViewModel.isLoad.value
    val destination = bundleAddedViewModel.destination.value
    bundleAddedViewModel.setValues()

    Scaffold(topBar = { TopAppBar( title = { Text("Bundle Added", textAlign = TextAlign.Center) }) }) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            if (loading) {
                LoadingDialog(isDisplayed = true)
            } else {
                Text(text = "Bundle ${ScannedInfo.heatNum} added to ${if (isLoad != null && isLoad) { "load" } else { "reception" }}")

                Text(text = "Bundles Remaining: ${bundleAddedViewModel.bundlesRemaining.value}")
                Button(onClick = {
                    navController.navigate(destination)
                }) {
                    Text(text = "OK", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}