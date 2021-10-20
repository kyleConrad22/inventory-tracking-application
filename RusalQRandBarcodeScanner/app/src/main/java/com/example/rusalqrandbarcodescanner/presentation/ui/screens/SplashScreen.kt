package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import android.util.Log
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.R
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.presentation.components.CircularIndeterminateProgressBar
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.screen_viewmodels.SplashViewModel
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@Composable
fun SplashScreen(navController: NavController, mainActivityVM: MainActivityViewModel) {

    val splashVM : SplashViewModel = viewModel(factory = SplashViewModel.SplashViewModelFactory((LocalContext.current.applicationContext as CodeApplication).invRepository, mainActivityVM))

    val loading = mainActivityVM.loading.value
    val destination = splashVM.destination.value

    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }

    if (!loading) {
        LaunchedEffect(key1 = true) {
            if (destination == Screen.InfoInputScreen.title) {
                navController.navigate(Screen.MainMenuScreen.title)
            }
            navController.navigate(destination)
        }
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
                CircularIndeterminateProgressBar(isDisplayed = loading)
            }

        }
}