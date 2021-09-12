package com.example.rusalqrandbarcodescanner

import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionRequired
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AskForCamPermission(navigateToSettingsScreen: () -> Unit) {
    //Track if the user doesn't want to see the rationale anymore
    var doNotShowRationale by rememberSaveable { mutableStateOf(false) }

    val cameraPermissionState =
        rememberPermissionState(permission = Manifest.permission.CAMERA)
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
