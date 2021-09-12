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

    private val scannedCodeViewModel : ScannedCodeViewModel by viewModels {
        ScannedCodeViewModelFactory((application as CodeApplication).repository)
    }

    private val currentInventoryViewModel : CurrentInventoryViewModel by viewModels {
        CurrentInventoryViewModelFactory((application as CodeApplication).invRepository)
    }

    private val userInputViewModel : UserInputViewModel by viewModels {
        UserInputViewModelFactory((application as CodeApplication).userRepository)
    }

    private class ImageAnalyzer: ImageAnalysis.Analyzer {
        @SuppressLint("UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            val mediaImage = imageProxy.image
            if (mediaImage != null) {
                val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
                val scanner = BarcodeScanning.getClient()
                val results = scanner.process(image)
                    .addOnSuccessListener { barcodes ->
                        for (barcode in barcodes) {
                            val bounds = barcode.boundingBox
                            val corners = barcode.cornerPoints

                            val rawValue = barcode.rawValue

                            val valueType = barcode.valueType
                            if (rawValue != null) {
                                ScannedInfo.setValues(rawValue)
                            }

                        }
                    }
                    .addOnFailureListener {
                        Log.e("Exceptions", "QR Code not found exception")
                    }
                    .addOnCompleteListener {imageProxy.close()}
            }
        }
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
    fun ConfirmButton(navController: NavHostController, str: String, dest: String) {
        Button(onClick = {
            if (str == "Load") {
                HttpRequestHandler.initUpdate(scannedCodeViewModel, currentInventoryViewModel)
            }
            navController.navigate(dest) }) {
            Text(text = "Confirm $str", modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun ScanButton(navController: NavHostController) {
        Button(onClick = { navController.navigate("scannerPage") }) {
            Text(text = "Scan Code", modifier = Modifier
                .padding(16.dp)
                .size(width = 200.dp, height = 20.dp)
                .align(Alignment.CenterVertically), textAlign = TextAlign.Center)
        }
    }

    @Composable
    fun RemoveEntryButton(navController: NavHostController) {
        Button(onClick = { navController.navigate("removeEntryPage") }) {
            Text(text = "Remove Entry", modifier = Modifier
                .padding(16.dp)
                .size(width = 200.dp, height = 20.dp)
                .align(Alignment.CenterVertically), textAlign = TextAlign.Center)
        }
    }

    @Composable
    fun AddButton(navController: NavHostController) {
        Button(onClick = {
            scannedCodeViewModel.insert(ScannedInfo.toScannedCode(userInputViewModel))
            navController.navigate("bundleAddedPage")
        }) {
            Text(text = "Add", modifier = Modifier.padding(16.dp))
        }
    }

    // Page which shows the options currently available to the user on the current load
    @Composable
    fun LoadOptionsPage(navController: NavHostController) {
        var resetDialog = remember { mutableStateOf(false) }
        var count by remember { mutableStateOf(scannedCodeViewModel.count.value) }
        val countObserver = Observer<Int> { it ->
            count = it
        }
        scannedCodeViewModel.count.observe(this@MainActivity, countObserver)

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Load Options:")
            Text(text = userInputViewModel.loader.value + userInputViewModel.order.value + " Load " + userInputViewModel.load.value)
            ScanButton(navController = navController)
            if (count != null && count!! > 0) {
                Button(onClick = {
                    resetDialog.value = true
                }) {
                    Text(text="Reset Load", modifier = Modifier
                        .padding(16.dp)
                        .size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically), textAlign = TextAlign.Center)
                }
                RemoveEntryButton(navController = navController)
            }
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    navController.navigateUp()
                }) {
                    Text(text="Back", modifier= Modifier.padding(16.dp))
                }
                if (count != null && count!! >0) {
                    ConfirmButton(navController = navController, str = "Load", dest = "reviewLoad")
                }
                if (resetDialog.value){
                    AlertDialog(onDismissRequest = {
                            resetDialog.value = false
                        }, title = {
                            Text(text="Reset Load Confirmation")
                        }, text = {
                            Text(text="Are you sure you would like to remove all bundles from this load? This cannot be undone.")
                        }, buttons = {
                             Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                 Button(onClick = {
                                     resetDialog.value = false
                                 }) {
                                     Text(text="Deny Reset", modifier = Modifier.padding(16.dp))
                                 }
                                 Button(onClick = {
                                     resetDialog.value = false
                                     scannedCodeViewModel.deleteAll()
                                 }) {
                                     Text(text="Confirm Reset", modifier = Modifier.padding(16.dp))
                                 }
                             }
                        }
                    )
                }
            }
        }
    }

    @Composable
    fun ScannerPage(navController: NavHostController) {
        var count by remember { mutableStateOf(scannedCodeViewModel.count.value) }
        val countObserver = Observer<Int>{ it ->
            count = it
        }
        var isLoad by remember { mutableStateOf(userInputViewModel.isLoad().value) }
        val loadObserver = Observer<Boolean> { it ->
            isLoad = it
        }
        var isReception by remember { mutableStateOf(userInputViewModel.isReception().value) }
        val receptionObserver = Observer<Boolean> { it ->
            isReception = it
        }
        userInputViewModel.isLoad().observe(this@MainActivity, loadObserver)
        userInputViewModel.isReception().observe(this@MainActivity, receptionObserver)
        scannedCodeViewModel.count.observe(this@MainActivity, countObserver)

        CameraPreview(modifier = Modifier.fillMaxSize(), navController = navController)
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom) {
            Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.End) {
                Button(modifier = Modifier
                    .padding(16.dp)
                    .alpha(if (false) {
                        1f
                    } else {
                        0f
                    }), onClick = {}) {Text(text="Toggle Flash")}
            }
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                Button(onClick = {
                    if (isLoad!!) {
                        navController.popBackStack("loadOptionsPage", inclusive = false)
                    } else if (isReception!!) {
                        navController.popBackStack("receptionOptionsPage", inclusive = false)
                    } else {
                        navController.popBackStack(Screen.MainMenuScreen.title, inclusive = false)
                    }
                }) {
                    Text(text="Back", modifier = Modifier.padding(16.dp))
                }
                Button(onClick = { navController.navigate("manualEntryPage") }) {
                    Text(text = "Manual Entry", modifier = Modifier.padding(16.dp))
                }
                if (count != null && count!! > 0) {
                    if (isLoad!!) {
                        ConfirmButton(navController = navController,
                            str = "load",
                            dest = "reviewLoad")
                    } else {
                        ConfirmButton(navController = navController,
                            str = "reception",
                            dest = "reviewReception")
                    }
                }
            }
            Spacer(modifier = Modifier.padding(30.dp))
        }
    }

    @Composable
    fun quantOptions(navController: NavHostController) {
        val heat = userInputViewModel.heat.value
        val quantity = userInputViewModel.quantity.value
        var blList by remember { mutableStateOf(currentInventoryViewModel.getBlList(heat).value)}
        val blListObserver = Observer<List<String>?>{ it ->
            blList = it
        }
        currentInventoryViewModel.getBlList(heat).observe(this@MainActivity, blListObserver)

        if (blList != null) {
            Column(modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceEvenly) {
                Text(text = """
                    Heat $heat is available in multiple quantities for BL ${blList!![0]}!
                    Ensure that the bundle being loaded is of quantity $quantity!
                    Would you like to add this bundle to the load?
                    """.trimMargin(), modifier = Modifier.padding(16.dp))
                Button(onClick = { navController.navigate("manualEntryPage")} ) {
                    Text(text="Back", modifier = Modifier.padding(16.dp))
                }
                AddButton(navController = navController)
            }
        }
    }

//Add tp setContent() method to see a full-screen preview
//Don't have to manage camera session state or dispose of images, binding to lifecycle is sufficient

    @Composable
    fun CameraPreview(
        modifier: Modifier = Modifier,
        cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
        scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
        navController: NavHostController

    ) {

        val lifecycleOwner = LocalLifecycleOwner.current
        AndroidView(
            modifier = modifier,
            factory = { context ->
                val previewView = PreviewView(context).apply {

                    this.scaleType = scaleType

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // Preview incorrectly scaled in Compose on some devices without following code
                    implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                }

                val imageAnalysis =
                    ImageAnalysis.Builder().setTargetResolution(Size(1080, 2310)).build()

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context),
                    { imageProxy ->
                        ImageAnalyzer().analyze(imageProxy)
                        if (ScannedInfo.qrCode != "") {
                            var result: ScannedCode?
                            currentInventoryViewModel.findByHeat(ScannedInfo.heatNum.replace("-", "")).observe(this@MainActivity,
                                { returnedCode ->
                                    if (returnedCode != null) {
                                        ScannedInfo.blNum = returnedCode.blNum.toString()
                                        ScannedInfo.quantity = returnedCode.quantity.toString()
                                    } else {
                                        ScannedInfo.blNum = "BL not found!"
                                    }

                                })
                            scannedCodeViewModel.findByBarcode(ScannedInfo.barCode).observe(this@MainActivity,
                                { returnedCode ->
                                    result = returnedCode
                                    val scanTime: String? = returnedCode?.scanTime
                                    if (result == null ) {
                                        if (ScannedInfo.blNum == userInputViewModel.bl.value && ScannedInfo.quantity == userInputViewModel.quantity.value ) {
                                            navController.navigate("scannedInfoReturn")
                                        } else if (ScannedInfo.blNum != userInputViewModel.bl.value) {
                                            navController.navigate("incorrectBl")
                                        } else {
                                            navController.navigate("incorrectQuantity")
                                        }
                                    } else if (scanTime != null) {
                                        Log.d("DEBUG", scanTime)
                                        navController.navigate("duplicateBundlePage/${scanTime}")
                                    }
                                })
                        }
                    })

                val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    // Preview
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    try {
                        // Must unbind the use-cases before rebinding them
                        cameraProvider.unbindAll()

                        cameraProvider.bindToLifecycle(lifecycleOwner,
                            cameraSelector,
                            imageAnalysis,
                            preview)
                    } catch (exc: Exception) {
                        Log.e(TAG, "Use case binding failed", exc)
                    }
                }, ContextCompat.getMainExecutor(context))

                previewView
            })
    }

    @Composable
    private fun Rationale(
        onDoNotShowRationale: () -> Unit,
        onRequestPermission: () -> Unit
    ) {
        Column {
            Text(text = "This app cannot be utilized without usage of the camera. Please grant permission.")
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                Button(onClick = onRequestPermission) {
                    Text(text = "Request permission")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onDoNotShowRationale) {
                    Text(text = "Don't show this message again")
                }
            }
        }
    }

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    private fun AskForCamPermission(navigateToSettingsScreen: () -> Unit) {
        //Track if the user doesn't want to see the rationale anymore
        var doNotShowRationale by rememberSaveable { mutableStateOf(false) }

        val cameraPermissionState =
            rememberPermissionState(permission = android.Manifest.permission.CAMERA)
        PermissionRequired(permissionState = cameraPermissionState,
            permissionNotGrantedContent = {
                if (doNotShowRationale) {
                    Text("Feature not available")
                } else {
                    Rationale(
                        onDoNotShowRationale = { doNotShowRationale = true },
                        onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
                    )
                }
            },
            permissionNotAvailableContent = { PermissionDenied(navigateToSettingsScreen) }
        ) {
            Text(text = "Camera permission granted", modifier = Modifier.alpha(0f))
        }
    }

    @Composable
    private fun PermissionDenied(
        navigateToSettingsScreen: () -> Unit
    ) {
        Column {
            Text(
                text = "Camera permission denied. Please note that the main functionality of this app requires usage of the camera." +
                        "Please grant us access on the Settings screen."
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = navigateToSettingsScreen) {
                Text(text = "Open Settings")
            }
        }
    }

    @Composable
    fun MainLayout() {
        val navController = rememberNavController()

        RusalQRAndBarcodeScannerTheme() {
            NavHost(navController = navController, startDestination = (Screen.MainMenuScreen.title)) {
                composable(Screen.MainMenuScreen.title) { MainMenuScreen(navController = navController, userInputViewModel, scannedCodeViewModel) }
                composable( Screen.DuplicateBundleScreen.title + "/{scanTime}") { backStackEntry ->
                    DuplicateBundleScreen(navController = navController, scanTime = backStackEntry.arguments?.getString("scanTime"), scannedCodeViewModel, userInputViewModel)
                }
                composable(Screen.BundleInfoScreen.title + "/{barcode}") { backStackEntry ->
                    BundleInfoScreen(navController = navController, barcode = backStackEntry.arguments?.getString("barcode"), scannedCodeViewModel, userInputViewModel)
                }
                composable(Screen.ConfirmationScreen.title) { ConfirmationScreen(navController, userInputViewModel) }
                composable(Screen.ReceptionReviewScreen.title) { ReceptionReviewScreen(navController, scannedCodeViewModel) }
                composable(Screen.LoadReviewScreen.title) { LoadReviewScreen(navController, scannedCodeViewModel, currentInventoryViewModel) }
                composable(Screen.BundleAddedScreen.title) { BundleAddedScreen(navController, scannedCodeViewModel, userInputViewModel) }
                composable(Screen.ScannedInfoScreen.title) { ScannedInfoScreen(navController, userInputViewModel, scannedCodeViewModel) }
                composable(Screen.ManualEntryScreen.title) { ManualEntryScreen(navController, userInputViewModel, currentInventoryViewModel, scannedCodeViewModel) }
                composable(Screen.ScannerScreen.title) { ScannerScreen(navController) }
                composable(Screen.ReceptionOptionsScreen.title) { ReceptionOptionsScreen(navController, scannedCodeViewModel) }
                composable(Screen.LoadOptionsScreen.title) { LoadOptionsScreen(navController) }
                composable(Screen.ReceptionInfoInputScreen.title) { ReceptionInfoInputScreen(navController, userInputViewModel) }
                composable(Screen.LoadInfoInputScreen.title) { LoadInfoInputScreen(navController, userInputViewModel) }
                composable(Screen.RemoveEntryScreen.title) { RemoveEntryScreen(navController, userInputViewModel, scannedCodeViewModel) }
                composable(Screen.IncorrectBlScreen.title) { IncorrectBlScreen(navController, userInputViewModel) }
                composable(Screen.IncorrectQuantityScreen.title) { IncorrectQuantityScreen(navController, userInputViewModel) }
                composable(Screen.ToBeImplementedScreen.title) { ToBeImplementedScreen(navController)}
                composable(Screen.BlOptionsScreen.title) { BlOptionsScreen(navController, currentInventoryViewModel, userInputViewModel) }
            }
        }
    }

//@Preview(showBackground = true)

    @Composable
    fun PreviewTest() {

        val navController = rememberNavController()

        RusalQRAndBarcodeScannerTheme {
            MainMenuScreen(navController = navController, userInputViewModel = userInputViewModel, scannedCodeViewModel = scannedCodeViewModel)
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
    object ReceptionReviewScreen: Screen("ReceptionReview")
    object LoadReviewScreen: Screen("LoadReview")
}