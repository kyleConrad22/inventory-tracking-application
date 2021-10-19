package com.example.rusalqrandbarcodescanner.presentation.components.progress

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.rusalqrandbarcodescanner.domain.models.SessionType

@Composable
fun SessionProgress(
    sessionType: SessionType,
    addedItems : Int,
    expectedItems : Int,
    partiallyIdentifiedItems : Int,
    newItems : Int
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(.7f).padding(8.dp).align(Alignment.CenterHorizontally),
            elevation = 2.dp,
            backgroundColor = Color.LightGray,
            shape = RoundedCornerShape(CornerSize(16.dp))
        ) {
            Row {
                Column(modifier = Modifier.padding(8.dp).fillMaxWidth()
                    .align(Alignment.CenterVertically)) {
                    Text(text = "$addedItems of $expectedItems Added to ${sessionType.type}" +
                            (if (partiallyIdentifiedItems > 0) "\n$partiallyIdentifiedItems Partially Identified Items Added" else "") +
                            if (newItems > 0) "\n$newItems New Items Added" else "",
                        modifier = Modifier.padding(8.dp).align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}