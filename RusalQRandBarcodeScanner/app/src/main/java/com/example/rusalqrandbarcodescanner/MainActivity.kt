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
import com.android.volley.toolbox.Volley
import com.example.rusalqrandbarcodescanner.presentation.ui.screens.*
import com.example.rusalqrandbarcodescanner.presentation.ui.theme.RusalQRAndBarcodeScannerTheme
import com.example.rusalqrandbarcodescanner.services.HttpRequestHandler
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel.MainActivityViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mainActivityVM: MainActivityViewModel by viewModels {
            MainActivityViewModelFactory((application as CodeApplication).invRepository, application)
        }

        HttpRequestHandler.requestQueue = Volley.newRequestQueue(this.applicationContext)

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
                    MainLayout(mainActivityVM)
                }
            }
        }
    }

    @ExperimentalAnimationApi
    @Suppress("UNCHECKED_CAST")
    @Composable
    fun MainLayout(mainActivityVM : MainActivityViewModel) {
        val navController = rememberNavController()

        RusalQRAndBarcodeScannerTheme() {
            NavHost(navController = navController,
                startDestination = (Screen.SplashScreen.title)) {
                composable(Screen.MainMenuScreen.title) { MainMenuScreen(navController, mainActivityVM) }
                composable(Screen.ReviewScreen.title) { ReviewScreen(navController, mainActivityVM) }
                composable(Screen.ManualEntryScreen.title) { ManualEntryScreen(navController, mainActivityVM) }
                composable(Screen.ScannerScreen.title) { ScannerScreen(navController, mainActivityVM) }
                composable(Screen.OptionsScreen.title) { OptionsScreen(navController, mainActivityVM) }
                composable(Screen.InfoInputScreen.title) { InfoInputScreen(navController, mainActivityVM) }
                composable(Screen.ToBeImplementedScreen.title) { ToBeImplementedScreen(navController) }
                composable(Screen.SplashScreen.title) { SplashScreen(navController, mainActivityVM) }
                composable(Screen.ReturnedItemScreen.title) { ReturnedItemScreen(navController, mainActivityVM) }
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
    object ReturnedItemScreen : Screen("ReturnedItem")
}