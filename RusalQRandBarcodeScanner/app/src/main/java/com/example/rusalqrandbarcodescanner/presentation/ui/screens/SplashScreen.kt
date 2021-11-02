package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.R
import com.example.rusalqrandbarcodescanner.presentation.components.CircularIndeterminateProgressBar
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.SplashState
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.SplashViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@Composable
fun SplashScreen(mainActivityVM: MainActivityViewModel, onNavRequest : (dest : String) -> Unit) {

    val splashVM : SplashViewModel = viewModel(factory = SplashViewModel.SplashViewModelFactory((LocalContext.current.applicationContext as CodeApplication).invRepository, mainActivityVM))

    val destination = splashVM.destination.value

    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    if (!splashVM.loading) {
        if (splashVM.uiState.value == SplashState.Recreation) RecreationAlertDialog(onDismiss = { onNavRequest(destination) })
        else LaunchedEffect(key1 = true) { onNavRequest(destination) }
    }

    // Animation Effect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1.5f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                }
            )
        )
    }

        // Image
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Image(painter = painterResource(id = R.drawable.splash_screen),
                    contentDescription = "Logo",
                    modifier = Modifier.scale(scale.value))
                CircularIndeterminateProgressBar(isDisplayed = true)
            }

        }
}

@Composable
fun RecreationAlertDialog(onDismiss : () -> Unit) {
    AlertDialog(
        title = { Text(text = "Recreating Session", textAlign = TextAlign.Center) },
        text = { Text(text = "It appears that your last session was closed before confirming the information added, attempting to recreate session from memory...", modifier = Modifier.padding(16.dp)) },
        onDismissRequest = onDismiss,
        buttons = {
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = onDismiss) {
                    Text(text = "Dismiss", modifier = Modifier.padding(16.dp))
                }
            }
        }
    )
}
