package com.example.labmobile.location

import android.Manifest
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.labmobile.util.RequirePermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

val TAG = "MyMap"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MyMap(lat: Double, lng: Double, modifier: Modifier, onLocationChanged: (Double, Double) -> Unit) {
    RequirePermission(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ),
        modifier = modifier
    ) {
        ShowMyMap(
            lat = lat,
            lng = lng,
            modifier = modifier,
            onLocationChanged = onLocationChanged
        )
    }
}

@Composable
fun ShowMyMap(lat: Double, lng: Double, modifier: Modifier, onLocationChanged: (Double, Double) -> Unit) {
    Log.d(TAG, "$lat - $lng")
    val markerState = rememberMarkerState(position = LatLng(lat, lng))
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerState.position, 10f)
    }

    Log.d(TAG, "Initial Camera Position: ${cameraPositionState.position.target}")
    GoogleMap(
        modifier = modifier,
        cameraPositionState = cameraPositionState,
        onMapClick = {
            Log.d(TAG, "onMapClick $it")
        },
        onMapLongClick = {
            Log.d(TAG, "onMapLongClick $it")
            markerState.position = it
            onLocationChanged(it.latitude, it.longitude)
        }
    ) {
        Marker(
            state = markerState,
            title = "User location title",
            snippet = "User location"
        )
    }
}