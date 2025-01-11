package com.example.labmobile.util

import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import androidx.compose.ui.Modifier
import com.google.accompanist.permissions.PermissionState

@ExperimentalPermissionsApi
@Composable
fun RequirePermission (
    permissions: List<String>,
    modifier: Modifier,
    content: @Composable () -> Unit = {}) {
    val permissionsState = rememberMultiplePermissionsState(permissions = permissions)
    if(permissionsState.allPermissionsGranted) {
        content()
    } else {
        Column(modifier = modifier) {
            Text(
                getTextToShowGivenPermissions(
                    permissionsState.revokedPermissions,
                    permissionsState.shouldShowRationale
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                Log.d("RequirePermission", "Requesting permissions...")
                permissionsState.launchMultiplePermissionRequest()
            }) {
                Text("Request Permissions")
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
private fun getTextToShowGivenPermissions(
    permissionStates: List<PermissionState>,
    shouldShowRationale: Boolean
) : String {
    val revokedPermissionsSize = permissionStates.size
    if(revokedPermissionsSize == 0) {
        return ""
    }
    val textToShow = StringBuilder().apply {
        append("The ")
    }
    for(i in permissionStates.indices) {
        textToShow.append(permissionStates[i].permission)
        when {
            revokedPermissionsSize > 1 && i == revokedPermissionsSize - 2 -> {
                textToShow.append(", and ")
            }
            i == revokedPermissionsSize - 1 -> {
                textToShow.append(" ")
            }
            else -> {
                textToShow.append(", ")
            }
        }
    }
    textToShow.append(if (revokedPermissionsSize == 1) "permission is" else "permissions are")
    textToShow.append(
        if(shouldShowRationale) {
            " important. Please grant all of them for the app to function properly"
        }
        else {
            " denied. The app cannot function without them."
        }
    )
    return textToShow.toString()
}