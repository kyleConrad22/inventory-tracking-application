package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.R
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewmodels.SplashScreenViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.SplashScreenViewModel.SplashScreenViewModelFactory
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val splashScreenViewModel : SplashScreenViewModel = viewModel(viewModelStoreOwner = LocalViewModelStoreOwner.current!!, key = "SplashScreenVM", factory = SplashScreenViewModelFactory((LocalContext.current.applicationContext as CodeApplication).repository))

    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
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
        delay(3000L)
        navController.navigate(Screen.MainMenuScreen.title)
    }

    // Image
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Image(painter = painterResource(id = R.drawable.splash_screen),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value))
    }
}