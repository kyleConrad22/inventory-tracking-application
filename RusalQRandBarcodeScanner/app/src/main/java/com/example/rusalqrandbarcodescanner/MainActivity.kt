package com.example.rusalqrandbarcodescanner

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rusalqrandbarcodescanner.presentation.ui.screens.*
import com.example.rusalqrandbarcodescanner.ui.theme.RusalQRAndBarcodeScannerTheme
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel.CurrentInventoryViewModelFactory
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel.ScannedCodeViewModelFactory
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel.UserInputViewModelFactory

@ExperimentalComposeUiApi
class MainActivity : ComponentActivity() {

    private val scannedCodeViewModel: ScannedCodeViewModel by viewModels {
        ScannedCodeViewModelFactory((application as CodeApplication).repository)
    }

    private val currentInventoryViewModel: CurrentInventoryViewModel by viewModels {
        CurrentInventoryViewModelFactory((application as CodeApplication).invRepository)
    }

    private val userInputViewModel: UserInputViewModel by viewModels {
        UserInputViewModelFactory((application as CodeApplication).userRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        HttpRequestHandler.initialize(currentInventoryViewModel)

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

    @Composable
    fun MainLayout() {
        val navController = rememberNavController()

        RusalQRAndBarcodeScannerTheme() {
            NavHost(navController = navController,
                startDestination = (Screen.MainMenuScreen.title)) {
                composable(Screen.MainMenuScreen.title) {
                    MainMenuScreen(navController = navController)
                }
                composable(Screen.DuplicateBundleScreen.title + "/{scanTime}") { backStackEntry ->
                    DuplicateBundleScreen(navController = navController,
                        scanTime = backStackEntry.arguments?.getString("scanTime"),
                        scannedCodeViewModel,
                        userInputViewModel)
                }
                composable(Screen.BundleInfoScreen.title + "/{barcode}") { backStackEntry ->
                    BundleInfoScreen(navController = navController,
                        barcode = backStackEntry.arguments?.getString("barcode"),
                        scannedCodeViewModel,
                        userInputViewModel)
                }
                composable(Screen.ConfirmationScreen.title) {
                    ConfirmationScreen(navController,
                        userInputViewModel)
                }
                composable(Screen.ReviewScreen.title) {
                    ReviewScreen(navController,
                        scannedCodeViewModel,
                        currentInventoryViewModel,
                        userInputViewModel)
                }
                composable(Screen.BundleAddedScreen.title) {
                    BundleAddedScreen(navController,
                        scannedCodeViewModel,
                        userInputViewModel)
                }
                composable(Screen.ScannedInfoScreen.title) {
                    ScannedInfoScreen(navController,
                        userInputViewModel,
                        scannedCodeViewModel)
                }
                composable(Screen.ManualEntryScreen.title) {
                    ManualEntryScreen(navController,
                        userInputViewModel,
                        currentInventoryViewModel,
                        scannedCodeViewModel)
                }
                composable(Screen.ScannerScreen.title) {
                    ScannerScreen(navController,
                        scannedCodeViewModel,
                        userInputViewModel,
                        currentInventoryViewModel)
                }
                composable(Screen.OptionsScreen.title) {
                    OptionsScreen(navController)
                }
                composable(Screen.InfoInputScreen.title) {
                    InfoInputScreen(navController,
                        userInputViewModel)
                }
                composable(Screen.RemoveEntryScreen.title) {
                    RemoveEntryScreen(navController,
                        userInputViewModel,
                        scannedCodeViewModel)
                }
                composable(Screen.IncorrectBlScreen.title) {
                    IncorrectBlScreen(navController,
                        userInputViewModel)
                }
                composable(Screen.IncorrectQuantityScreen.title) {
                    IncorrectQuantityScreen(navController,
                        userInputViewModel)
                }
                composable(Screen.ToBeImplementedScreen.title) { ToBeImplementedScreen(navController) }
                composable(Screen.BlOptionsScreen.title) {
                    BlOptionsScreen(navController,
                        currentInventoryViewModel,
                        userInputViewModel)
                }
                composable(Screen.QuantityOptionsScreen.title) {
                    QuantityOptionsScreen(navController,
                        currentInventoryViewModel,
                        userInputViewModel,
                        scannedCodeViewModel)
                }
            }
        }
    }
}

sealed class Screen(val title: String) {
    object MainMenuScreen: Screen("MainMenu")
    object ScannerScreen: Screen("Scanner")
    object InfoInputScreen: Screen("InfoInput")
    object BlOptionsScreen: Screen("BlOptions")
    object BundleAddedScreen: Screen("BundleAdded")
    object BundleInfoScreen: Screen("BundleInfo")
    object ConfirmationScreen: Screen("Confirmation")
    object DuplicateBundleScreen: Screen("DuplicateBundle")
    object IncorrectBlScreen: Screen("IncorrectBl")
    object IncorrectQuantityScreen: Screen("IncorrectQuantity")
    object OptionsScreen: Screen("Options")
    object ManualEntryScreen: Screen("ManualEntry")
    object RemoveEntryScreen: Screen("RemoveEntry")
    object ScannedInfoScreen: Screen("ScannedInfo")
    object ToBeImplementedScreen: Screen("ToBeImplemented")
    object ReviewScreen: Screen("Review")
    object QuantityOptionsScreen: Screen("QuantityOptionsScreen")
}