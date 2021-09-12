package com.example.rusalqrandbarcodescanner.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel

@Composable
fun ReceptionReviewScreen(navController: NavHostController, scannedCodeViewModel: ScannedCodeViewModel) {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly) {
        Text(text = "Review Reception:", modifier = Modifier.padding(16.dp))
        GetCodeListView(navController, scannedCodeViewModel = scannedCodeViewModel)
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                navController.navigateUp()
            }) {
                Text(text = "Back", modifier = Modifier.padding(16.dp))
            }
            Button(onClick = {
                /*TODO - Add Reception Confirmation Logic */
                navController.popBackStack(Screen.MainMenuScreen.title, inclusive = false)
            }) {
                Text(text="Confirm Reception", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun GetCodeListView(navController: NavHostController, scannedCodeViewModel: ScannedCodeViewModel) {
    var codes = remember { scannedCodeViewModel.allCodes.value }
    val codeObserver = Observer<List<ScannedCode>> { codeList ->
        codes = codeList
    }
    scannedCodeViewModel.allCodes.observe(LocalLifecycleOwner.current, codeObserver)

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
fun CodeListItem(scannedCode: ScannedCode, navController: NavHostController) {
    Card(
        modifier= Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
            .clickable(onClick = {

                navController.navigate("bundleInfo/${scannedCode.barCode}")
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