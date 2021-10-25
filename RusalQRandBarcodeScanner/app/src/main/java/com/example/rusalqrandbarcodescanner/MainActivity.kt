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
import androidx.navigation.NavOptions
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
                composable(Screen.MainMenuScreen.title) { MainMenuScreen(
                    mainActivityVM = mainActivityVM,
                    onNavRequest = { dest ->
                        navController.navigate(dest)
                    }
                )}
                composable(Screen.ReviewScreen.title) { ReviewScreen(
                    mainActivityVM = mainActivityVM,
                    onBack = {
                        if (!navController.popBackStack(Screen.OptionsScreen.title, inclusive = false)) navController.popBackStack(Screen.MainMenuScreen.title, inclusive = false)
                    }, onConfirm = {
                        navController.popBackStack(Screen.ManualEntryScreen.title, inclusive = false)
                    }
                )}
                composable(Screen.ManualEntryScreen.title) { ManualEntryScreen(
                    mainActivityVM = mainActivityVM,
                    onBack = {
                        navController.popBackStack()
                    }, onRetrieve = {
                        navController.navigate(Screen.ReturnedItemScreen.title)
                    }
                )}
                composable(Screen.ScannerScreen.title) { ScannerScreen(
                    mainActivityVM =  mainActivityVM,
                    onBack = {
                        navController.popBackStack()
                    }, onScan = {
                        navController.navigate(Screen.ReturnedItemScreen.title)
                    }, onManualRequest = {
                        navController.navigate(Screen.ManualEntryScreen.title)
                    }
                )}
                composable(Screen.OptionsScreen.title) { OptionsScreen(
                    mainActivityVM = mainActivityVM,
                    onBack = {
                        navController.popBackStack()
                    }, onReview = {
                        navController.navigate(Screen.ReviewScreen.title)
                    }, onScanRequest = {
                        navController.navigate(Screen.ScannerScreen.title)
                    }
                )}
                composable(Screen.InfoInputScreen.title) { InfoInputScreen(
                    mainActivityVM = mainActivityVM,
                    onBack = {
                        navController.popBackStack(Screen.MainMenuScreen.title, inclusive = false)
                    }, onConfirm = {
                        navController.navigate(Screen.OptionsScreen.title)
                    }
                )}
                composable(Screen.ToBeImplementedScreen.title) { ToBeImplementedScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )}
                composable(Screen.SplashScreen.title) { SplashScreen(navController, mainActivityVM) }
                composable(Screen.ReturnedItemScreen.title) { ReturnedItemScreen(
                    mainActivityVM = mainActivityVM,
                    onDismissNav = {
                        navController.popBackStack(Screen.OptionsScreen.title, inclusive = false)
                    }, onReviewNav = {
                        navController.navigate(Screen.ReviewScreen.title, NavOptions.Builder().setPopUpTo(Screen.OptionsScreen.title, inclusive = false).build())
                    }
                )}
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