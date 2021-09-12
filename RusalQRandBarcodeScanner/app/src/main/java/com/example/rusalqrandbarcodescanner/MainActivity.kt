package com.example.rusalqrandbarcodescanner

import com.google.accompanist.permissions.*

import android.annotation.SuppressLint
import androidx.compose.foundation.lazy.items
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rusalqrandbarcodescanner.screens.*
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.screens.LoadInfoInputScreen
import com.example.rusalqrandbarcodescanner.screens.MainMenuScreen
import com.example.rusalqrandbarcodescanner.screens.ReceptionInfoInputScreen
import com.example.rusalqrandbarcodescanner.screens.ScannerScreen
import com.example.rusalqrandbarcodescanner.ui.theme.RusalQRAndBarcodeScannerTheme
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel.CurrentInventoryViewModelFactory
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel.ScannedCodeViewModelFactory
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel.UserInputViewModelFactory
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import androidx.camera.view.PreviewView as PreviewView

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
                composable(Screen.ReceptionOptionsScreen.title) {
                    ReceptionOptionsScreen(navController,
                        scannedCodeViewModel)
                }
                composable(Screen.LoadOptionsScreen.title) {
                    LoadOptionsScreen(navController,
                        scannedCodeViewModel,
                        userInputViewModel,
                        currentInventoryViewModel)
                }
                composable(Screen.ReceptionInfoInputScreen.title) {
                    ReceptionInfoInputScreen(navController,
                        userInputViewModel)
                }
                composable(Screen.LoadInfoInputScreen.title) {
                    LoadInfoInputScreen(navController,
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
    object LoadInfoInputScreen: Screen("LoadInfoInput")
    object ReceptionInfoInputScreen: Screen("ReceptionInfoInput")
    object BlOptionsScreen: Screen("BlOptions")
    object BundleAddedScreen: Screen("BundleAdded")
    object BundleInfoScreen: Screen("BundleInfo")
    object ConfirmationScreen: Screen("Confirmation")
    object DuplicateBundleScreen: Screen("DuplicateBundle")
    object IncorrectBlScreen: Screen("IncorrectBl")
    object IncorrectQuantityScreen: Screen("IncorrectQuantity")
    object LoadOptionsScreen: Screen("LoadOptions")
    object ReceptionOptionsScreen: Screen("ReviewOptions")
    object ManualEntryScreen: Screen("ManualEntry")
    object RemoveEntryScreen: Screen("RemoveEntry")
    object ScannedInfoScreen: Screen("ScannedInfo")
    object ToBeImplementedScreen: Screen("ToBeImplemented")
    object ReviewScreen: Screen("Review")
    object QuantityOptionsScreen: Screen("QuantityOptionsScreen")
}