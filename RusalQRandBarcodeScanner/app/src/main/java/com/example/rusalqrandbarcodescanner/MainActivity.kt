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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.rusalqrandbarcodescanner.database.CurrentInventoryLineItem
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
        UserInputViewModelFactory()
    }

    private fun setTime(): String{
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM_dd_yyyy HH:mm:ss")
        val timeNow: LocalDateTime = LocalDateTime.now()
        return formatter.format(timeNow)
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
        var codes = remember { scannedCodeViewModel.allCodes.value }
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
    fun BackButton(navController: NavHostController, dest: String) {
        Button(onClick = { navController.navigate(dest) }) {
            Text(text = "Back", modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun LoadConfirmButton(navController: NavHostController) {
        var loadConfirmVis = remember { userInputViewModel.loadConfirmIsVisible().value }
        val loadObserver = Observer<Boolean> { it ->
            loadConfirmVis = it
        }
        userInputViewModel.loadConfirmIsVisible().observe(this@MainActivity, loadObserver)

        Button(modifier = Modifier.alpha(if(loadConfirmVis!!){1f} else{0f}), onClick = {
            if (loadConfirmVis!!) {
                navController.navigate("loadOptionsPage")
            }
        }) {
            Text(text = "Confirm Load Info", modifier = Modifier.padding(16.dp))
        }
    }

    @Composable
    fun ReceptionConfirmButton(navController: NavHostController) {
        var receptionIsVis = remember { userInputViewModel.receptionConfirmIsVisible().value }
        val receptionObserver = Observer<Boolean> { it ->
            receptionIsVis = it
        }
        userInputViewModel.receptionConfirmIsVisible().observe(this@MainActivity, receptionObserver)

        Button(modifier = Modifier.alpha(if(receptionIsVis!!){1f} else{0f}), onClick = {
            if (receptionIsVis!!) {
                navController.navigate("receptionOptionsPage")
            }
        }) {
            Text(text = "Confirm Reception Info", modifier = Modifier.padding(16.dp))
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
    fun DenyButton(navController: NavHostController) {
        Button(onClick = {
            ScannedInfo.clearValues()
            navController.navigate("scannerPage")
        }) {
            Text(text = "Deny", modifier = Modifier.padding(16.dp))
        }
    }

    // TextField which takes user input and assigns it to the bundles variable in userInputViewModel
    @ExperimentalComposeUiApi
    @Composable
    fun BundlesInput(focusManager: FocusManager) {
        var bundleQty by remember { mutableStateOf(userInputViewModel.bundles.value) }
        val bundleObserver = Observer<String> { it ->
            bundleQty = it
        }
        userInputViewModel.bundles.observe(this@MainActivity, bundleObserver)

        bundleQty?.let { qty ->
            OutlinedTextField(singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                value = qty,
                onValueChange = { userInputViewModel.bundles.value = it },
                label = { Text(text = "Bundles: ") })
        }
    }

    // TextField which takes user input and assigns it to the bl variable in userInputViewModel
    @ExperimentalComposeUiApi
    @Composable
    fun BlInput(focusManager: FocusManager) {
        var bl by remember { mutableStateOf(userInputViewModel.bl.value) }
        val blObserver = Observer<String>{ it ->
            bl = it
        }
        userInputViewModel.bl.observe(this@MainActivity, blObserver)

        bl?.let { blNum ->
            OutlinedTextField(
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                value = blNum,
                onValueChange = { userInputViewModel.bl.value = it },
                label = { Text(text = "BL: ") })
        }
    }

    // TextField which takes user input and assigns it to the loader variable in userInputViewModel
    @ExperimentalComposeUiApi
    @Composable
    fun LoaderInput(focusManager: FocusManager) {
        var loader by remember { mutableStateOf(userInputViewModel.loader.value) }
        val loaderObserver = Observer<String>{ it ->
            loader = it
        }
        userInputViewModel.loader.observe(this@MainActivity, loaderObserver)

        loader?.let { loadIt ->
            OutlinedTextField(singleLine=true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus(true) }),
                value = loadIt,
                onValueChange = { userInputViewModel.loader.value = it },
                label = { Text(text = "Loader: ") })
        }
    }

    // TextField which takes user input and assigns it to the vessel variable in userInputViewModel
    @ExperimentalComposeUiApi
    @Composable
    fun VesselInput(focusManager: FocusManager) {
        var vessel by remember { mutableStateOf(userInputViewModel.vessel.value) }
        val vesselObserver = Observer<String> { it ->
            vessel = it
        }
        userInputViewModel.vessel.observe(this@MainActivity, vesselObserver)

        vessel?.let { ves ->
            OutlinedTextField(singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                value = ves,
                onValueChange = { userInputViewModel.vessel.value = it },
                label = { Text(text = "Vessel: ") })
        }
    }

    // TextField which takes user input and assigns it to the quantity variable in userInputViewModel
    @ExperimentalComposeUiApi
    @Composable
    fun QuantityInput(focusManager: FocusManager) {
        var quantity by remember { mutableStateOf(userInputViewModel.quantity.value) }
        val quantityObserver = Observer<String> { it ->
            quantity = it
        }
        userInputViewModel.quantity.observe(this@MainActivity, quantityObserver)

        quantity?.let { quant ->
            OutlinedTextField(
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                value = quant,
                onValueChange = { userInputViewModel.quantity.value = it },
                label = { Text(text = "Quantity Per Bundle: ")}
            )
        }
    }

    // TextField which takes user input and assigns it to the checker variable in userInputViewModel
    @ExperimentalComposeUiApi
    @Composable
    fun CheckerInput(focusManager: FocusManager) {
        var checker by remember { mutableStateOf(userInputViewModel.checker.value) }
        val checkerObserver = Observer<String> { it ->
            checker = it
        }
        userInputViewModel.checker.observe(this@MainActivity, checkerObserver)

        checker?.let { check ->
            OutlinedTextField(singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus(true) }),
                value = check,
                onValueChange = { userInputViewModel.checker.value = it },
                label = { Text(text = "Checker: ") })
        }
    }

    // TextField which takes user input and assigns it to the heat variable in userInputViewModel
    @ExperimentalComposeUiApi
    @Composable
    fun heatNumberInput(focusManager: FocusManager): String? {
        var heat by remember { mutableStateOf(userInputViewModel.heat.value) }
        val heatObserver = Observer<String>{ it ->
            heat = it
        }
        userInputViewModel.heat.observe(this@MainActivity, heatObserver)

        heat?.let { heatNum ->
            OutlinedTextField(
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.clearFocus(true) }),
                value = heatNum,
                onValueChange = { userInputViewModel.heat.value = it },
                label = { Text(text = "Heat Number: ") })
        }
        return heat
    }

    // TextField which takes user input and assigns it to the order variable in userInputViewModel
    @ExperimentalComposeUiApi
    @Composable
    fun WorkOrderInput(focusManager: FocusManager) {
        var workOrder by remember { mutableStateOf(userInputViewModel.order.value) }
        val orderObserver = Observer<String>{ it ->
            workOrder = it
        }
        userInputViewModel.order.observe(this@MainActivity, orderObserver)

        workOrder?.let { ord ->
            OutlinedTextField(singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions( onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                value = ord,
                onValueChange = { userInputViewModel.order.value = it },
                label = { Text(text = "Work Order: ") })
        }
    }

    //TextField which takes user input and assigns it to the load variable in userInputViewModel
    @ExperimentalComposeUiApi
    @Composable
    fun LoadNumberInput(focusManager: FocusManager) {
        var loadNum by remember { mutableStateOf(userInputViewModel.load.value) }
        val loadObserver = Observer<String> { it ->
            loadNum = it
        }
        userInputViewModel.load.observe(this@MainActivity, loadObserver)

        loadNum?.let { load ->
            OutlinedTextField(singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext =  { focusManager.moveFocus(FocusDirection.Down) }),
                value = load,
                onValueChange = { userInputViewModel.load.value = it },
                label = { Text(text = "Load Number: ") })
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
            Button(onClick = { navController.navigate("loadInfoPage") }) {
                Text(text="New Load", modifier = Modifier
                        .padding(16.dp)
                        .size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
            Button(onClick = { navController.navigate("receptionInfoPage") }) {
                Text(text="New Reception", modifier = Modifier
                        .padding(16.dp)
                        .size(width = 200.dp, height = 20.dp)
                        .align(Alignment.CenterVertically),
                    textAlign = TextAlign.Center)
            }
        }
    }

    // Page which takes user input necessary to create a new load
    @ExperimentalComposeUiApi
    @Composable
    fun LoadInfoPage(navController: NavHostController) {
        val focusManager = LocalFocusManager.current

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Load Info:")
            WorkOrderInput(focusManager)
            LoadNumberInput(focusManager)
            BundlesInput(focusManager)
            BlInput(focusManager)
            QuantityInput(focusManager)
            LoaderInput(focusManager)
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                BackButton(navController = navController, dest = "mainMenu")
                LoadConfirmButton(navController = navController)
            }
        }
    }

    // Page which shows the options currently available to the user on the current load
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

    // Page which prompts takes user input necessary to create a new reception
    @Composable
    fun ReceptionInfoPage(navController: NavHostController) {
        val focusManager = LocalFocusManager.current

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text = "Reception Info:")
            VesselInput(focusManager)
            CheckerInput(focusManager)
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                BackButton(navController = navController, dest = "mainMenu")
                ReceptionConfirmButton(navController = navController)
            }
        }
    }

    // Page which shows the options currently available to the user on the current reception
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
                Button(onClick = { navController.navigate("manualEntryPage") }) {
                    Text(text = "Manual Entry", modifier = Modifier.padding(16.dp))
                }
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
        val focusManager = LocalFocusManager.current

        Column(modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text="Manual Heat Number Search: ", modifier = Modifier.padding(16.dp))
            var heat = heatNumberInput(focusManager)
            Row(modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly) {
                BackButton(navController = navController, dest = "scannerPage")
                Button(onClick = {
                    if (heat != null) {
                        userInputViewModel.updateHeat(heat!!.replace("\n", "").replace("-", "")
                            .replace(" ", ""))
                        heat = userInputViewModel.heat.value
                    }
                    if (heat?.length == 6){
                        var result: List<CurrentInventoryLineItem>?
                        currentInventoryViewModel.findByBaseHeat("%${heat!!}%").observe(this@MainActivity, { returnedVal ->
                            result = returnedVal
                            if (result != null){
                                val blList = mutableListOf<String>()
                                val quantityList = mutableListOf<String>()
                                for (item in result!!){
                                    if (item.blNum!! !in blList) {
                                        blList.add(item.blNum)
                                    }
                                    if (item.quantity!! !in quantityList){
                                        quantityList.add(item.quantity)
                                    }
                                }
                                if (blList.size == 1 && quantityList.size == 1) {
                                    var barcode: String
                                    var baseList: List<CurrentInventoryLineItem>?
                                    currentInventoryViewModel.findByBarcodes("%${heat!!}u%").observe(this@MainActivity, { list ->
                                        baseList = list
                                        if (baseList != null) {
                                            for (item in baseList!!) {
                                                Log.d("DEBUG", item.barcode)
                                            }
                                        }
                                        barcode = if (baseList == null) {
                                            "${heat!!}u1"
                                        } else {
                                            "${heat!!}u${baseList!!.size + 1}"
                                        }
                                        Log.d("DEBUG", "Barcode set as $barcode")
                                        val inventoryLineItem = CurrentInventoryLineItem(
                                            heatNum = heat,
                                            packageNum = "N/A",
                                            grossWeightKg = "N/A",
                                            netWeightKg = "N/A",
                                            quantity = quantityList[0],
                                            dimension = result!![0].dimension,
                                            grade = result!![0].grade,
                                            certificateNum = result!![0].certificateNum,
                                            blNum = blList[0],
                                            barcode = barcode,
                                            workOrder = userInputViewModel.order.value,
                                            loadNum = userInputViewModel.load.value,
                                            loader = userInputViewModel.loader.value,
                                            loadTime = setTime()
                                        )

                                        ScannedInfo.getValues(inventoryLineItem)

                                        currentInventoryViewModel.insert(inventoryLineItem)

                                        navController.navigate("scannedInfoReturn")
                                    })
                                } else if (blList.size != 1) {
                                    /*TODO*/
                                    navController.navigate("blOptions")
                                    /*Present bl options to loader and ask for them to make a selection*/
                                } else {
                                    /*TODO*/
                                    navController.navigate("toBeImplemented")
                                    /*Ask for loader to verify that there are the requested number of pieces on this bundle, have them type the amount*/

                                }
                            } else {
                                Log.d("DEBUG", "Query returned a null value!")
                            }
                        })
                    } else {
                        var returnedCode: ScannedCode?
                        scannedCodeViewModel.findByHeat(heat!!)
                            .observe(this@MainActivity, { code ->
                                returnedCode = code
                                if (returnedCode == null) {
                                    currentInventoryViewModel.findByHeat(heat!!)
                                        .observe(this@MainActivity, { inventoryItem ->
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
                                                Log.d("DEBUG",
                                                    "Heat number returned a null reference!")
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
                Button(onClick = { navController.navigate("loadOptionsPage")}) {
                    Text(text="No", modifier = Modifier.padding(16.dp))
                }
                Button(onClick = { navController.navigate("confirmResetPage")}) {
                    Text(text="Yes", modifier = Modifier.padding(16.dp))
                }
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
        val focusManager = LocalFocusManager.current

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
            val heat = heatNumberInput(focusManager)
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
    fun ToBeImplemented(navController: NavHostController) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text="This feature has yet to be implemented!")
            Button(onClick={
                navController.navigate("scannerPage")
            }) {
                Text(text="Back to Scanner Live Feed", modifier = Modifier.padding(16.dp))
            }

        }
    }

    @Composable
    fun BlOptions(navController: NavHostController) {
        val heat = userInputViewModel.heat.value
        var blList = remember { currentInventoryViewModel.getBlList(heat!!).value }
        val blObserver = Observer<List<String>> { items ->
            blList = items
        }

        currentInventoryViewModel.getBlList(heat!!).observe(this, blObserver)

        Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.SpaceEvenly) {
            Text(text="Retrieved BL numbers:", modifier = Modifier.padding(16.dp))
            LazyColumn(
                modifier = Modifier.background(Color.LightGray).size(400.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),

            ) {
                if (blList != null) {
                    items(
                        items = blList!!,
                        itemContent = { BlListItem(bl = it) }
                    )
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Bottom){
                Button(onClick =
                { navController.navigate("manualEntryPage") } ) {
                    Text(text="Back to Manual Entry", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }

    @Composable
    fun BlListItem(bl: String) {
        Card(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp).fillMaxWidth(),
            elevation = 2.dp,
            backgroundColor = Color.Black,
            shape = RoundedCornerShape(corner = CornerSize(16.dp))
        ) {
            Row {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .align(Alignment.CenterVertically)) {
                    Text(text=bl, style = typography.h6)
                }
            }
        }
    }

    @OptIn(ExperimentalComposeUiApi::class)
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
            composable("toBeImplemented") { ToBeImplemented(navController = navController)}
            composable("blOptions") { BlOptions(navController = navController) }
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