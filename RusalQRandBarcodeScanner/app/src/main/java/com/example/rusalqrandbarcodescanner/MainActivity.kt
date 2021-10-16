package com.example.rusalqrandbarcodescanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rusalqrandbarcodescanner.presentation.ui.screens.*
import com.example.rusalqrandbarcodescanner.presentation.ui.theme.RusalQRAndBarcodeScannerTheme
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel.MainActivityViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory((application as CodeApplication).invRepository, application)
    }

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            RusalQRAndBarcodeScannerTheme {
                //A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    //CameraPreview(modifier=Modifier.fillMaxSize())
                    AskForCamPermission(
                        navigateToSettingsScreen = {
                            startActivity(
                                Intent(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", packageName, null)
                                )
                            )
                        }
                    )
                    MainLayout()
                }
            }
        }
    }

    @ExperimentalAnimationApi
    @Suppress("UNCHECKED_CAST")
    @Composable
    fun MainLayout() {
        val navController = rememberNavController()

        RusalQRAndBarcodeScannerTheme() {
            NavHost(navController = navController,
                startDestination = (Screen.SplashScreen.title)) {
                composable(Screen.MainMenuScreen.title) { MainMenuScreen(navController) }
                composable(Screen.ReviewScreen.title) {
                    ReviewScreen(navController,
                        mainActivityViewModel)
                }
                composable(Screen.ManualEntryScreen.title) { ManualEntryScreen(navController) }
                composable(Screen.ScannerScreen.title) { ScannerScreen(navController) }
                composable(Screen.OptionsScreen.title) { OptionsScreen(navController) }
                composable(Screen.InfoInputScreen.title) { InfoInputScreen(navController) }
                composable(Screen.ToBeImplementedScreen.title) { ToBeImplementedScreen(navController) }
                composable(Screen.SplashScreen.title) { SplashScreen(navController) }
                composable(Screen.ReturnedBundleScreen.title) { ReturnedBundleScreen(navController) }
            }
        }
    }
}

sealed class Screen(val title: String) {
    object SplashScreen: Screen("SplashScreen")
    object MainMenuScreen: Screen("MainMenu")
    object ScannerScreen: Screen("Scanner")
    object InfoInputScreen: Screen("InfoInput")
    object OptionsScreen: Screen("Options")
    object ManualEntryScreen: Screen("ManualEntry")
    object ToBeImplementedScreen: Screen("ToBeImplemented")
    object ReviewScreen: Screen("Review")
    object ReturnedBundleScreen : Screen("ReturnedBundle")
}