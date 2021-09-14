package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.ScannedInfo
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

@ExperimentalComposeUiApi
@Composable
fun ScannerScreen(navController: NavHostController, scannedCodeViewModel: ScannedCodeViewModel, userInputViewModel: UserInputViewModel, currentInventoryViewModel: CurrentInventoryViewModel) {
    val lifecycleOwner = LocalLifecycleOwner.current

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
    userInputViewModel.isLoad().observe(lifecycleOwner, loadObserver)
    userInputViewModel.isReception().observe(lifecycleOwner, receptionObserver)
    scannedCodeViewModel.count.observe(lifecycleOwner, countObserver)

    val type = if (isLoad != null && isLoad!!) { "Load" } else { "Reception" }

    CameraPreview(modifier = Modifier.fillMaxSize(), navController = navController, currentInventoryViewModel = currentInventoryViewModel, scannedCodeViewModel = scannedCodeViewModel, userInputViewModel = userInputViewModel)
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
                }), onClick = {}) { Text(text="Toggle Flash") }
        }
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                when {
                    isLoad!! -> {
                        navController.popBackStack(Screen.LoadOptionsScreen.title, inclusive = false)
                    }
                    isReception!! -> {
                        navController.popBackStack(Screen.ReceptionOptionsScreen.title, inclusive = false)
                    }
                    else -> {
                        navController.popBackStack(Screen.MainMenuScreen.title, inclusive = false)
                    }
                }
            }) {
                Text(text="Back", modifier = Modifier.padding(16.dp))
            }
            Button(onClick = { navController.navigate(Screen.ManualEntryScreen.title) }) {
                Text(text = "Manual Entry", modifier = Modifier.padding(16.dp))
            }
            if (count != null && count!! > 0) {
                Button(onClick = {
                    navController.navigate(Screen.ReviewScreen.title)
                }) {
                    Text("Confirm $type", modifier = Modifier.padding(16.dp))
                }
            }
        }
        Spacer(modifier = Modifier.padding(30.dp))
    }
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

@ExperimentalComposeUiApi
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    navController: NavHostController,
    currentInventoryViewModel: CurrentInventoryViewModel,
    scannedCodeViewModel: ScannedCodeViewModel,
    userInputViewModel: UserInputViewModel
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
                        currentInventoryViewModel.findByHeat(ScannedInfo.heatNum.replace("-", "")).observe(lifecycleOwner,
                            { returnedCode ->
                                if (returnedCode != null) {
                                    ScannedInfo.blNum = returnedCode.blNum.toString()
                                    ScannedInfo.quantity = returnedCode.quantity.toString()
                                } else {
                                    ScannedInfo.blNum = "BL not found!"
                                }

                            })
                        scannedCodeViewModel.findByBarcode(ScannedInfo.barCode).observe(lifecycleOwner,
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
                    Log.e(ContentValues.TAG, "Use case binding failed", exc)
                }
            }, ContextCompat.getMainExecutor(context))

            previewView
        })
}