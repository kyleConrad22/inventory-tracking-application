package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.HttpRequestHandler
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ReviewViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ReviewViewModel.ReviewViewModelFactory
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel


@Composable
fun ReviewScreen(navController: NavHostController, scannedCodeViewModel: ScannedCodeViewModel, currentInventoryViewModel: CurrentInventoryViewModel) {
    val application = LocalContext.current.applicationContext

    val reviewViewModel : ReviewViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "reviewVM", factory = ReviewViewModelFactory((application as CodeApplication).repository, application.invRepository, application.userRepository))

    val isLoad = reviewViewModel.isLoad.value
    val loadType = reviewViewModel.loadType.value

    val confirmDialog = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        Text(text = "Review $loadType:", modifier = Modifier.padding(16.dp))
        GetCodeListView(navController)
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                navController.navigateUp()
            }) {
                Text(text="Back", modifier = Modifier.padding(16.dp))
            }
            Button(onClick = {
                if (isLoad) {
                    HttpRequestHandler.initUpdate(scannedCodeViewModel, currentInventoryViewModel)
                } else {
                    /*TODO - Add Reception Confirmation Logic */
                }
                reviewViewModel.deleteAll()
                confirmDialog.value = true
            }) {
                Text(text="Confirm $loadType", modifier = Modifier.padding(16.dp))
            }
        }
    }
    if (confirmDialog.value) {
        AlertDialog(
            onDismissRequest = {
                navController.popBackStack(Screen.MainMenuScreen.title, inclusive = true)
            },
            title = { Text(text = "$loadType Confirmation") },
            text = { Text(text = "$loadType Confirmed") },
            buttons = {
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly) {
                    Button(onClick = {
                        navController.popBackStack(Screen.MainMenuScreen.title, inclusive = true)
                    }) { Text(text = "Ok", modifier = Modifier.padding(16.dp)) }
                }
            }
        )
    }
}



@Composable
private fun GetCodeListView(navController: NavHostController) {
    val application = LocalContext.current.applicationContext

    val reviewViewModel : ReviewViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "reviewVM", factory = ReviewViewModelFactory((application as CodeApplication).repository, application.invRepository, application.userRepository))

    var codes = remember { reviewViewModel.codes.value }
    val codeObserver = Observer<List<ScannedCode>> { codeList ->
        codes = codeList
    }
    reviewViewModel.codes.observe(LocalLifecycleOwner.current, codeObserver)

    LazyColumn (
        modifier= Modifier
            .background(Color.LightGray)
            .size(400.dp),
        contentPadding= PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        if (codes != null) {
            items(
                items = codes!!,
                itemContent = {
                    CodeListItem(scannedCode = it, navController)
                }
            )
        }
    }
}

@Composable
private fun CodeListItem(scannedCode: ScannedCode, navController: NavHostController) {
    Card(
        modifier= Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable(onClick = {

                navController.navigate("${Screen.BundleInfoScreen.title}/${scannedCode.barCode}")
            })
            .fillMaxWidth(),
        elevation = 2.dp,
        backgroundColor = Color.Black,
        shape = RoundedCornerShape(corner = CornerSize(16.dp))
    ) {
        Row {
            Column(modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .align(Alignment.CenterVertically)) {
                Text(text = "Heat: ${scannedCode.heatNum!!} || BL: ${scannedCode.bl!!}" , style = MaterialTheme.typography.h6)
            }
        }
    }
}