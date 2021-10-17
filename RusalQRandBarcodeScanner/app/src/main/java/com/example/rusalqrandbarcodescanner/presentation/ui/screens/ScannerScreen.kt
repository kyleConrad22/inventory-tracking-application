package com.example.rusalqrandbarcodescanner.presentation.ui.screens

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.rusalqrandbarcodescanner.CodeApplication
import com.example.rusalqrandbarcodescanner.util.ScannedInfo
import com.example.rusalqrandbarcodescanner.Screen
import com.example.rusalqrandbarcodescanner.viewmodels.MainActivityViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun ScannerScreen(navController: NavHostController, mainActivityVM: MainActivityViewModel) {

    CameraPreview(modifier = Modifier.fillMaxSize(), navController = navController, mainActivityVM = mainActivityVM)
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom) {
        Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = {
                navController.popBackStack()
            }) {
                Text(text = "Back", modifier = Modifier.padding(16.dp))
            }
            Button(onClick = { navController.navigate(Screen.ManualEntryScreen.title) }) {
                Text(text = "Manual Entry", modifier = Modifier.padding(16.dp))
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
                        if (ScannedInfo.heatNum == "") {
                            val bounds = barcode.boundingBox
                            val corners = barcode.cornerPoints
                            val rawValue = barcode.rawValue

                            val valueType = barcode.valueType
                            if (rawValue != null) {
                                ScannedInfo.setValues(rawValue)
                                ScannedInfo.isScanned = true
                            }
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

@DelicateCoroutinesApi
@ExperimentalComposeUiApi
@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    scaleType: PreviewView.ScaleType = PreviewView.ScaleType.FILL_CENTER,
    navController: NavHostController,
    mainActivityVM : MainActivityViewModel
) {

    val lifecycleOwner = LocalLifecycleOwner.current

    var camera : Camera? by remember { mutableStateOf(null) }

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

            if (camera == null) {
                val imageAnalysis =
                    ImageAnalysis.Builder().setTargetResolution(Size(1080, 2310)).build()

                imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(context),
                    { imageProxy ->
                        ImageAnalyzer().analyze(imageProxy)
                        if (ScannedInfo.heatNum != "" && ScannedInfo.isScanned) {
                            mainActivityVM.heatNum.value = ScannedInfo.heatNum
                            navController.navigate(Screen.ReturnedBundleScreen.title)
                            ScannedInfo.isScanned = false
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

                        camera = cameraProvider.bindToLifecycle(lifecycleOwner,
                            cameraSelector,
                            imageAnalysis,
                            preview)

                    } catch (exc: Exception) {
                        Log.e(ContentValues.TAG, "Use case binding failed", exc)
                    }
                }, ContextCompat.getMainExecutor(context))
            }
            previewView
        })

    if (camera != null && camera!!.cameraInfo.hasFlashUnit()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.End
        ) {
                var enabled by remember { mutableStateOf(false) }
                IconButton(onClick = {
                    camera!!.cameraControl.enableTorch(!enabled)
                    enabled = !enabled
                }) {
                    Icon(
                        if (enabled)
                            Icons.Filled.FlashlightOn
                        else
                            Icons.Filled.FlashlightOff,

                        "Toggle Flashlight",
                        tint = MaterialTheme.colors.primary
                    )
                }
        }

    }
}