package com.example.rusalqrandbarcodescanner.presentation.components.autocomplete

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp

internal typealias ItemSelected<T> = (T) -> Unit

@Stable
interface AutoCompleteScope<T : AutoCompleteEntity> : AutoCompleteDesignScope {
    var isSearching : Boolean
    fun filter(query : String)
    fun onItemSelected(block : ItemSelected<T> = {})
}

@Stable
interface AutoCompleteDesignScope {
    var boxWidthPercentage : Float
    var shouldWrapContentHeight : Boolean
    var boxMaxHeight : Dp
    var boxBorderStroke : BorderStroke
    var boxShape : Shape
}