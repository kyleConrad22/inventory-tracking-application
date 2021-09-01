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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.rusalqrandbarcodescanner.database.ScannedCode
import com.example.rusalqrandbarcodescanner.ui.theme.RusalQRAndBarcodeScannerTheme
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel
import com.example.rusalqrandbarcodescanner.viewModels.CurrentInventoryViewModel.CurrentInventoryViewModelFactory
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel
import com.example.rusalqrandbarcodescanner.viewModels.ScannedCodeViewModel.ScannedCodeViewModelFactory
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel
import com.example.rusalqrandbarcodescanner.viewModels.UserInputViewModel.UserInputViewModelFactory
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import androidx.camera.view.PreviewView as PreviewView


class MainActivity : ComponentActivity() {

    private val scannedCodeViewModel : ScannedCodeViewModel by viewModels {
        ScannedCodeViewModelFactory((application as CodeApplication).repository)
    }

    private val currentInventoryViewModel : CurrentInventoryViewModel by viewModels {
        CurrentInventoryViewModelFactory((application as CodeApplication).invRepository)
    }

    private val userInputViewModel : UserInputViewModel by viewModels {
        UserInputViewModelFactory()
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

    @Composable
    fun GetCodeListView() {
        var codes = remember { scannedCodeViewModel.allCodes.value}
        val codeObserver = Observer<List<ScannedCode>> { codeList ->
            codes = codeList
        }
        scannedCodeViewModel.allCodes.observe(this, codeObserver)

        LazyColumn (
            modifier= Modifier
                .background(Color.LightGray)
                .size(400.dp),
            contentPadding= PaddingValues(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (codes != null) {
                items(
                    items = codes!!,
                    itemContent = {
                        CodeListItem(scannedCode = it)
                    }
                )
            }
        }
    }

    @Composable
    fun CodeListItem(scannedCode: ScannedCode) {
        Card(
            modifier= Modifier
                .padding(horizontal = 8.dp, vertical = 8.dp)
                .fillMaxWidth(),
            elevation = 2.dp,
            backgroundColor = Color.Black,
            shape = RoundedCornerShape(corner = CornerSize(16.dp))
        ) {
            Row {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)) {
                    scannedCode.heatNum?.let { Text(text = it, style = typography.h6) }
                }
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

                    RusalScannerDriver()
                }
            }
        }
    }

    @Composable
    fun NewLoadButton(navController: NavHostController) {
        Button(onClick = { navController.navigate("loadInfoPage") }) {
            Text(text = "New Load", modifier = Modifier
                .padding(16.dp)
                .size(width = 200.dp, height = 20.dp)
                .align(Alignment.CenterVertically), textAlign = TextAlign.Center)
        }
    }

    @Composable
    fun NewReceptionButton(navController: NavHostController) {
        Button(onClick = { navController.navigate("receptionInfoPage") }) {
            Text(text = "New Reception", modifier = Modifier
                .padding(16.dp)
                .size(width = 200.dp, height = 20.dp)
                .align(Alignment.CenterVertically), textAlign = TextAlign.Center)
        }
    }

    @Composable
    fun BackButton(navController: NavHostController, dest: String) {
        Button(onClick = { navController.navigate(dest) }) {
            Text(text = "Back", modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun LoadConfirmButton(navController: NavHostController, loader: String, workOrder: String, loadNum: String, bundleQty: String, blExp: String, quantity: String) {
        val isVisible: Boolean = !(loader == "" || workOrder == "" || loadNum == "" || bundleQty == "" || blExp == "" || quantity == "")
        Button(modifier = Modifier.alpha(if(isVisible){1f} else{0f}), onClick = {
            if (isVisible) {
                navController.navigate("loadOptionsPage")
            }
            userInputViewModel.update(loader = loader, order = workOrder, load = loadNum, bundles = bundleQty, bl = blExp, checker = "", vessel = "", heat = "", quantity = quantity)
        }) {
            Text(text = "Confirm Load Info", modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun ReceptionConfirmButton(navController: NavHostController, checker: String, vessel: String) {
        val isVisible: Boolean = !(checker == "" || vessel == "")
        Button(modifier = Modifier.alpha(if(isVisible){1f} else{0f}), onClick = {
            if (isVisible) {
                navController.navigate("receptionOptionsPage")
            }
            userInputViewModel.update(loader = "", order = "", load = "", bundles = "", bl = "", vessel = vessel, checker = vessel, heat = "", quantity = "")
        }) {
            Text(text = "Confirm Reception Info", modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun ConfirmButton(navController: NavHostController, str: String, dest: String) {
        Button(onClick = {
            if (str == "Load") {
                HttpRequestHandler.initUpdate(scannedCodeViewModel)
            }
            navController.navigate(dest) }) {
            Text(text = "Confirm $str", modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun OkButton(navController: NavHostController, dest: String) {
        Button(onClick = { navController.navigate(dest)
        if (dest == "scannerPage") {
            ScannedInfo.clearValues()
        }}) {
            Text(text = "OK", modifier = Modifier.padding(16.dp))
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
    fun ResetLoadButton(navController: NavHostController) {
        Button(onClick = { navController.navigate("resetConfirmationPage") }) {
            Text(text = "Reset Load", modifier = Modifier
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
    fun ManualButton(navController: NavHostController) {
        Button(onClick = { navController.navigate("manualEntryPage") }) {
            Text(text = "Manual Entry", modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun NoButton(navController: NavHostController, dest: String) {
        Button(onClick = { navController.navigate(dest) }) {
            Text(text = "No", modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun YesButton(navController: NavHostController, dest: String) {
        Button(onClick = { navController.navigate(dest) }) {
            Text(text = "Yes", modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun DenyButton(navController: NavHostController) {
        Button(onClick = {
            ScannedInfo.clearValues()
            navController.navigate("scannerPage")
        }) {
            Text(text = "Deny", modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun bundlesInput(): String? {
        var bundleQty by remember { mutableStateOf(userInputViewModel.bundles.value) }

        bundleQty?.let { qty ->
            OutlinedTextField(singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = qty,
                onValueChange = { bundleQty = it },
                label = { Text(text = "Bundles: ") })
        }

        return bundleQty
    }

    @Composable
    fun blInput(): String? {
        var bl by remember { mutableStateOf(userInputViewModel.bl.value) }

        bl?.let { blNum ->
            OutlinedTextField(singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters),
                value = blNum,
                onValueChange = { bl = it },
                label = { Text(text = "BL: ") })
        }
        return bl
    }

    @Composable
    fun loaderInput(): String? {
        var loader by remember { mutableStateOf(userInputViewModel.loader.value) }

        loader?.let { load ->
            OutlinedTextField(singleLine=true,
                value = load,
                onValueChange = { loader = it },
                label = { Text(text = "Loader: ") })
        }
        return loader
    }

    @Composable
    fun vesselInput(): String? {
        var vessel by remember { mutableStateOf(userInputViewModel.vessel.value) }

        vessel?.let { ves ->
            OutlinedTextField(singleLine = true,
                value = ves,
                onValueChange = { vessel = it },
                label = { Text(text = "Vessel: ") })
        }
        return vessel
    }

    @Composable
    fun quantityInput(): String? {
        var quantity by remember { mutableStateOf(userInputViewModel.quantity.value) }

        quantity?.let { quant ->
            OutlinedTextField(singleLine = true,
                value = quant,
                onValueChange = { quantity = it },
                label = { Text(text = "Quantity Per Bundle: ")}
            )
        }
        return quantity
    }

    @Composable
    fun checkerInput(): String? {
        var checker by remember { mutableStateOf(userInputViewModel.checker.value) }

        checker?.let { check ->
            OutlinedTextField(singleLine = true,
                value = check,
                onValueChange = { checker = it },
                label = { Text(text = "Checker: ") })
        }
        return checker
    }

    @Composable
    fun heatNumberInput(): String? {
        var heat by remember { mutableStateOf(userInputViewModel.heat.value) }

        heat?.let { heatNum ->
            OutlinedTextField(singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = heatNum,
                onValueChange = { heat = it },
                label = { Text(text = "Heat Number: ") })
        }
        return heat
    }

    @Composable
    fun workOrderInput(): String? {
        var workOrder by remember { mutableStateOf(userInputViewModel.order.value) }

        workOrder?.let { ord ->
            OutlinedTextField(singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters),
                value = ord,
                onValueChange = { workOrder = it },
                label = { Text(text = "Work Order: ") })
        }
        return workOrder
    }

    @Composable
    fun loadNumberInput(): String? {
        var loadNum by remember { mutableStateOf(userInputViewModel.load.value) }

        loadNum?.let { load ->
            OutlinedTextField(singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                value = load,
                onValueChange = { loadNum = it },
                label = { Text(text = "Load Number: ") })
        }
        return loadNum
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

    @Composable
    fun BundleAddedText() {
        val heat = ScannedInfo.heatNum
        val addType = if (userInputViewModel.loader.value != "") { "load" } else { "reception" }

        Text(text = "Bundle $heat added to $addType")
    }

    @Composable
    fun MainMenu(navController: NavHostController) {
        scannedCodeViewModel.deleteAll()
        userInputViewModel.removeValues()
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Main Menu", modifier = Modifier.padding(16.dp), fontSize = 25.sp)
            NewLoadButton(navController = navController)
            NewReceptionButton(navController = navController)
        }
    }

    @Composable
    fun LoadInfoPage(navController: NavHostController) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Load Info:")
            val workOrder = workOrderInput()
            val loadNum = loadNumberInput()
            val bundleQty = bundlesInput()
            val blExp = blInput()
            val quantity = quantityInput()
            val loader = loaderInput()
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                BackButton(navController = navController, dest = "mainMenu")
                if (bundleQty != null && loader != null && loadNum != null && workOrder != null && blExp != null && quantity != null) {
                    LoadConfirmButton(navController = navController,
                        loader = loader,
                        workOrder = workOrder,
                        loadNum = loadNum,
                        bundleQty = bundleQty,
                        blExp = blExp,
                        quantity = quantity)
                } else {
                    throw NullPointerException("One of the assigned values is null!")
                }
            }
        }
    }

    @Composable
    fun LoadOptionsPage(navController: NavHostController) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Load Options:")
            Text(text = userInputViewModel.loader.value + userInputViewModel.order.value + " Load " + userInputViewModel.load.value)
            ScanButton(navController = navController)
            ResetLoadButton(navController = navController)
            RemoveEntryButton(navController = navController)
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                BackButton(navController = navController, dest = "loadInfoPage")
                ConfirmButton(navController = navController, str = "Load", dest = "reviewLoad")
            }
        }
    }

    @Composable
    fun ReceptionInfoPage(navController: NavHostController) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Reception Info:")
            var vessel = vesselInput()
            var checker = checkerInput()
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                BackButton(navController = navController, dest = "mainMenu")
                if (vessel != null && checker != null){
                    ReceptionConfirmButton(navController = navController,
                        vessel = vessel,
                        checker = checker)
                } else { throw NullPointerException("One of the assigned values is null!")}
            }
        }
    }

    @Composable
    fun ReceptionOptionsPage(navController: NavHostController) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Reception Options:")
            ScanButton(navController = navController)
            RemoveEntryButton(navController = navController)
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                BackButton(navController = navController, dest = "receptionInfoPage")
                ConfirmButton(navController = navController,
                    str = "Reception",
                    dest = "reviewReception")
            }
        }
    }

    @Composable
    fun ScannerPage(navController: NavHostController) {
        val dest: String = if (userInputViewModel.loader.value != "") {
            "loadOptionsPage"
        } else {
            "receptionOptionsPage"
        }
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
                BackButton(navController = navController, dest = dest)
                ManualButton(navController = navController)
                if (userInputViewModel.loader.value != "") {
                    ConfirmButton(navController = navController, str = "load", dest="reviewLoad")
                } else {
                    ConfirmButton(navController = navController, str = "reception", dest = "reviewReception")
                }
            }
            Spacer(modifier = Modifier.padding(30.dp))
        }
    }

    @Composable
    fun ManualEntryPage(navController: NavHostController) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text="Manual Heat Number Search: ", modifier = Modifier.padding(16.dp))
            val heat = heatNumberInput()
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                BackButton(navController = navController, dest = "scannerPage")
                Button(onClick = {
                    if (heat != null){
                        var returnedCode: ScannedCode?
                        scannedCodeViewModel.findByHeat(heat).observe(this@MainActivity, { code ->
                            returnedCode = code
                            if (returnedCode == null) {
                                currentInventoryViewModel.findByHeat(heat.replace("\n", "")
                                    .replace("-", "").replace(" ", ""))
                                    .observe(this@MainActivity, { inventoryItem ->
                                        userInputViewModel.updateHeat(heat)
                                        if (inventoryItem != null) {
                                            ScannedInfo.getValues(inventoryItem)
                                            Log.d("DEBUG", "Retrieved non-null reference")
                                            if (ScannedInfo.blNum == userInputViewModel.bl.value && ScannedInfo.quantity == userInputViewModel.quantity.value) {
                                                navController.navigate("scannedInfoReturn")
                                            } else if (ScannedInfo.blNum != userInputViewModel.bl.value) {
                                                navController.navigate("incorrectBl")
                                            } else {
                                                navController.navigate("incorrectQuantity")
                                            }
                                        } else {
                                            Log.d("DEBUG", "Heat number returned a null reference!")
                                        }
                                    })
                            } else if (returnedCode?.scanTime != null) {
                                navController.navigate("duplicateBundlePage/${returnedCode?.scanTime}")
                            }
                        })
                    }

                }){
                    Text(text="Retrieve Bundle Info", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }

    @Composable
    fun ScannedInfoReturn(navController: NavHostController) {

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text("Scanned Bundle Information:", modifier = Modifier.padding(16.dp))
            val heatNum = ScannedInfo.heatNum
            val blNum = ScannedInfo.blNum
            Text("Heat / Cast Number: $heatNum")
            Text("BL: $blNum")
            val addType = if (userInputViewModel.loader.value != "") { "load" } else { "reception" }
            Text(text = "Add to ${addType}?", modifier = Modifier.padding(16.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                DenyButton(navController = navController)
                AddButton(navController = navController)
            }
        }
    }

    @Composable
    fun BundleAddedPage(navController: NavHostController) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            BundleAddedText()
            var count = remember { scannedCodeViewModel.count.value }
            val countObserver = Observer<Int> { rowCount ->
                count = rowCount
            }
            scannedCodeViewModel.count.observe(this@MainActivity, countObserver)
            if (count != null) {
                Text(text = "Bundles Remaining: ${Integer.parseInt(userInputViewModel.bundles.value!!) - count!!}")
                val dest = if (Integer.parseInt(userInputViewModel.bundles.value!!) - count!! == 0) {
                    "reviewLoad"
                } else {
                    "scannerPage"
                }
                OkButton(navController = navController, dest = dest)
            }
        }
    }

    @Composable
    fun ConfirmResetPage(navController: NavHostController) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Are you sure you would like to reset this load?",
                modifier = Modifier.padding(16.dp))
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                NoButton(navController = navController, dest = "loadOptionsPage")
                YesButton(navController = navController, dest = "confirmResetPage")
            }
        }
    }

    @Composable
    fun ResetConfirmationPage(navController: NavHostController) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Reset Complete", modifier = Modifier.padding(16.dp))
            OkButton(navController = navController, dest = "loadInfoPage")
        }
    }

    @Composable
    fun ReviewLoad(navController: NavHostController) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Review Load:", modifier = Modifier.padding(16.dp))
            GetCodeListView()
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                BackButton(navController = navController, dest = "loadOptionsPage")
                ConfirmButton(navController = navController,
                    str = "Load",
                    dest = "ConfirmationPage")
            }
        }
    }

    @Composable
    fun ReviewReception(navController: NavHostController) {
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Review Reception:", modifier = Modifier.padding(16.dp))
            GetCodeListView()
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                BackButton(navController = navController, dest = "receptionOptionsPage")
                ConfirmButton(navController = navController,
                    str = "Reception",
                    dest = "ConfirmationPage")
            }
        }
    }

    @Composable
    fun ConfirmationPage(navController: NavHostController) {
        val text: String = if (userInputViewModel.loader.value != "") {
            "Load Confirmed"
        } else {
            "Reception Confirmed"
        }
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = text, modifier = Modifier.padding(16.dp))
            OkButton(navController = navController, dest = "mainMenu")
        }
    }

    @Composable
    fun RemoveEntryPage(navController: NavHostController) {
        val backDest: String = if (userInputViewModel.loader.value != "") {
            "loadOptionsPage"
        } else {
            "receptionOptionsPage"
        }
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Enter Heat / Cast Number of Entry to be Removed:",
                modifier = Modifier.padding(16.dp))
            val heat = heatNumberInput()
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                BackButton(navController = navController, dest = backDest)
                Button(onClick = {
                    if( heat != null) {
                        scannedCodeViewModel.findByHeat(heat).observe(this@MainActivity, {item ->
                            if (item != null) {
                                scannedCodeViewModel.delete(item)
                                navController.navigate(backDest)
                            } else {
                                Log.d("DEBUG", "Heat number returned a null value!")
                            }
                        })
                    }
                }) {
                    Text(text="Confirm Removal")
                }
            }
        }
    }

    @Composable
    fun RemovalConfirmationPage(navController: NavHostController) {
        val dest: String
        val text: String
        if (userInputViewModel.loader.value != "") {
            dest = "loadOptionsPage"
            text = "load"
        } else {
            dest = "receptionOptionsPage"
            text = "load"
        }
        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "${userInputViewModel.heat.value} has been removed from $text.",
                modifier = Modifier.padding(16.dp))
            OkButton(navController = navController, dest = dest)
        }
    }

    @Composable
    fun DuplicateBundlePage(navController: NavHostController, scanTime: String?) {
        Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly){
            Text(text="Bundle ${ScannedInfo.heatNum} has already been scanned!", modifier = Modifier.padding(16.dp))
            Text(text="Last scan was at: $scanTime", modifier = Modifier.padding(16.dp))
            Row(modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly) {
                DenyButton(navController = navController)
                AddButton(navController = navController)
            }
        }
    }

    @Composable
    fun IncorrectBl(navController: NavHostController) {
        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text="Incorrect BL!", modifier = Modifier.padding(16.dp))
            Text(text="Requested BL is ${userInputViewModel.bl.value}, the scanned bundle has BL of ${ScannedInfo.blNum}", modifier = Modifier.padding(16.dp))
            Text(text="Put bundle away and scan another!", modifier = Modifier.padding(16.dp))
            Button(onClick = {
                ScannedInfo.clearValues()
                navController.navigate("scannerPage")
            }) {
                Text(text="Back to Scanner Live Feed", modifier = Modifier.padding(16.dp))
            }
        }
    }

    @Composable
    fun IncorrectQuantity(navController: NavHostController) {
        Column (modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text="Incorrect quantity per bundle!", modifier = Modifier.padding(16.dp))
            Text(text="Requested quantity per bundle is ${userInputViewModel.quantity.value}, the scanned bundle has a quantity per bundle of ${ScannedInfo.quantity}", modifier = Modifier.padding(16.dp))
            Text(text="Put bundle away and scan another!", modifier=Modifier.padding(16.dp))
            Button(onClick = {
                ScannedInfo.clearValues()
                navController.navigate("scannerPage")
            }) {
                Text(text = "Back to Scanner Live Feed", modifier = Modifier.padding(16.dp))
            }
        }
    }

    @Composable
    fun NavigationHost(navController: NavHostController) {

        NavHost(navController = navController, startDestination = "mainMenu") {
            composable("mainMenu") { MainMenu(navController = navController) }
            composable("duplicateBundlePage/{scanTime}") {backStackEntry ->
                DuplicateBundlePage(navController = navController, backStackEntry.arguments?.getString("scanTime"))
            }
            composable("ConfirmationPage") { ConfirmationPage(navController = navController) }
            composable("reviewReception") { ReviewReception(navController = navController) }
            composable("reviewLoad") { ReviewLoad(navController = navController) }
            composable("resetConfirmationPage") { ResetConfirmationPage(navController = navController) }
            composable("confirmResetPage") { ConfirmResetPage(navController = navController) }
            composable("bundleAddedPage") { BundleAddedPage(navController = navController) }
            composable("scannedInfoReturn") { ScannedInfoReturn(navController = navController) }
            composable("manualEntryPage") { ManualEntryPage(navController = navController) }
            composable("scannerPage") { ScannerPage(navController = navController) }
            composable("receptionOptionsPage") { ReceptionOptionsPage(navController = navController) }
            composable("loadOptionsPage") { LoadOptionsPage(navController = navController) }
            composable("receptionInfoPage") { ReceptionInfoPage(navController = navController) }
            composable("loadInfoPage") { LoadInfoPage(navController = navController) }
            composable("removeEntryPage") { RemoveEntryPage(navController = navController) }
            composable("removalConfirmationPage") { RemovalConfirmationPage(navController = navController) }
            composable("incorrectBl") { IncorrectBl(navController = navController) }
            composable("incorrectQuantity") { IncorrectQuantity(navController = navController)}
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
    fun RusalScannerDriver() {
        val navController = rememberNavController()

        RusalQRAndBarcodeScannerTheme() {
            NavigationHost(navController = navController)
        }
    }

//@Preview(showBackground = true)

    @Composable
    fun PreviewTest() {

        val navController = rememberNavController()

        RusalQRAndBarcodeScannerTheme {
            MainMenu(navController = navController)
        }

    }
}