package com.example.labmobile.location

import android.Manifest
import android.app.Application
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labmobile.util.RequirePermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyLocation(modifier: Modifier = Modifier) {
    RequirePermission(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION),
        modifier = Modifier.fillMaxSize()
    ) {
        ShowMyLocation(
            modifier = modifier
        )
    }
}

@Composable
fun ShowMyLocation(modifier: Modifier) {
    val myLocationViewModel = viewModel<MyLocationViewModel> (
        factory = MyLocationViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )

    val location = myLocationViewModel.uiState
    if(location != null) {
        MyMap(location.latitude, location.longitude, modifier, {p1, p2 -> })
    } else {
        LinearProgressIndicator()
    }
}