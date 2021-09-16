package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.viewModels.ScannedInfoViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ScannedInfoViewModel.ScannedInfoViewModelFactory

@Composable
fun ScannedInfoScreen(navController: NavHostController) {
    val application = LocalContext.current.applicationContext
    val scannedInfoViewModel : ScannedInfoViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "scannedInfoVM", factory = ScannedInfoViewModelFactory((application as CodeApplication).userRepository, application.repository))

    val loading = scannedInfoViewModel.loading.value
    val isClicked = remember { mutableStateOf(false) }
    scannedInfoViewModel.setIsLoad()

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        if (loading) {
            LoadingDialog(isDisplayed = true)
        } else {
            Text("Scanned Bundle Information:", modifier = Modifier.padding(16.dp))
            Text("Heat / Cast Number: ${scannedInfoViewModel.heatNum}")
            Text("BL: ${scannedInfoViewModel.blNum}")
            Text(text = "Add to ${if (scannedInfoViewModel.isLoad.value!!) { "load" } else { "reception" }}?", modifier = Modifier.padding(16.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    scannedInfoViewModel.clearValues()
                    navController.popBackStack()
                }) {
                    Text("Deny", modifier = Modifier.padding(16.dp))
                }
                Button(onClick = {
                    scannedInfoViewModel.addBundle()
                    isClicked.value = true
                }) {
                    Text("Add", modifier = Modifier.padding(16.dp))
                }
            }
        }
        if (!loading && isClicked.value) {
            navController.navigate(Screen.BundleAddedScreen.title)
        }
    }
}