package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.presentation.components.LoadingDialog
import com.example.rusalqrandbarcodescanner.viewModels.BlOptionsViewModel
import com.example.rusalqrandbarcodescanner.viewModels.BlOptionsViewModel.BlOptionsViewModelFactory
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel

@Composable
fun BlOptionsScreen(navController: NavHostController) {
    val application = LocalContext.current.applicationContext
    val blOptionsViewModel : BlOptionsViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "blOptionsVM", factory = BlOptionsViewModelFactory((application as CodeApplication).userRepository, application.invRepository))

    val loading = blOptionsViewModel.loading.value
    val blList = blOptionsViewModel.blList.value
    if (blList.isEmpty()) {
        blOptionsViewModel.setBlList()
    }
    Scaffold(topBar = { TopAppBar(title = { Text("BL Options", textAlign = TextAlign.Center) }) }) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            if (loading) {
                LoadingDialog(isDisplayed = true)
            } else {
                Text(text = "Retrieved BL numbers:", modifier = Modifier.padding(16.dp))

                LazyColumn(
                    modifier = Modifier
                        .background(Color.LightGray)
                        .size(400.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),

                    ) {
                    items(
                        items = blList,
                        itemContent = { BlListItem(bl = it) }
                    )
                }
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom) {
                    Button(onClick =
                    {
                        navController.popBackStack(Screen.ManualEntryScreen.title,
                            inclusive = false)
                    }) {
                        Text(text = "Back to Manual Entry", modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun BlListItem(bl: String) {
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
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
                Text(text=bl, style = MaterialTheme.typography.h6)
            }
        }
    }
}