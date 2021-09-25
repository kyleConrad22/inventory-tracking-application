package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun LoadedHeatsScreen(navController : NavController, heats : List<String>) {
    Scaffold(topBar = { TopAppBar(title = { Text("Heats Loaded", textAlign = TextAlign.Center) }) }) {

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text="Bundle cannot be added to load! Maximum of three heats may be loaded for ingot shipments! Currently loaded heats are: ")
            HeatList(heats)
            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Ok", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
private fun HeatList(heats: List<String>) {
    LazyColumn(modifier = Modifier
        .background(Color.LightGray)
        .size(400.dp), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
        items(
            items = heats,
            itemContent = {
                HeatListItem(heat = it)
            }
        )
    }
}

@Composable
private fun HeatListItem(heat : String) {
    Card(modifier = Modifier
        .padding(vertical = 8.dp, horizontal = 8.dp)
        .fillMaxWidth(), elevation = 2.dp, backgroundColor = Color.Black, shape = RoundedCornerShape(corner = CornerSize(16.dp))) {
        Row {
           Column(modifier = Modifier
               .padding(16.dp)
               .fillMaxWidth()
               .align(Alignment.CenterVertically)) {
               Text(text="Heat: $heat", style = MaterialTheme.typography.h6)
           }
        }
    }
}
