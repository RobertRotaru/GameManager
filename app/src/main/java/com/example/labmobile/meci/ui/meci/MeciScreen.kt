package com.example.labmobile.meci.ui.meci

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labmobile.R
import com.example.labmobile.core.Result
import com.example.labmobile.core.TAG
import com.example.labmobile.location.MyMap
import com.example.labmobile.meci.ui.meci.MeciViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeciScreen(meciId: String?, onClose: () -> Unit) {
    val meciViewModel = viewModel<MeciViewModel>(factory = MeciViewModel.Factory(meciId))
    val meciUiState = meciViewModel.uiState
    var name by rememberSaveable { mutableStateOf(meciUiState.meci.name) }
    var pretBilet by rememberSaveable { mutableStateOf(meciUiState.meci.pretBilet) }
    var hasStarted by rememberSaveable { mutableStateOf(meciUiState.meci.hasStarted) }
    var startDate by rememberSaveable { mutableStateOf(meciUiState.meci.startDate) }
    var latitude by rememberSaveable { mutableDoubleStateOf(meciUiState.meci.latitude) }
    var longitude by rememberSaveable { mutableDoubleStateOf(meciUiState.meci.longitude) }
    var isDatePickerDialogOpen by remember { mutableStateOf(false) }
    Log.d("MeciScreen", "recompose")

    LaunchedEffect(meciUiState.submitResult) {
        Log.d("MeciScreen", "Submit = ${meciUiState.submitResult}")
        if(meciUiState.submitResult is Result.Success) {
            Log.d("MeciScreen", "Closing screen")
            onClose()
        }
    }

    var nameInitialized by remember { mutableStateOf(meciId == null) }
    LaunchedEffect(meciId, meciUiState.loadResult) {
        Log.d("ItemScreen", "Name initialized = ${meciUiState.loadResult}");
        if (nameInitialized) {
            return@LaunchedEffect
        }
        if (!(meciUiState.loadResult is Result.Loading)) {
            name = meciUiState.meci.name
            nameInitialized = true
        }
    }

    var pretBiletInitialized by remember { mutableStateOf(meciId == null) }
    LaunchedEffect(meciId, meciUiState.loadResult) {
        Log.d("ItemScreen", "PretBilet initialized = ${meciUiState.loadResult}");
        if (pretBiletInitialized) {
            return@LaunchedEffect
        }
        if (!(meciUiState.loadResult is Result.Loading)) {
            pretBilet = meciUiState.meci.pretBilet
            pretBiletInitialized = true
        }
    }

    var hasStartedInitialized by remember { mutableStateOf(meciId == null) }
    LaunchedEffect(meciId, meciUiState.loadResult) {
        Log.d("ItemScreen", "HasStarted initialized = ${meciUiState.loadResult}");
        if (hasStartedInitialized) {
            return@LaunchedEffect
        }
        if (!(meciUiState.loadResult is Result.Loading)) {
            hasStarted = meciUiState.meci.hasStarted
            hasStartedInitialized = true
        }
    }

    var startDateInitialized by remember { mutableStateOf(meciId == null) }
    LaunchedEffect(meciId, meciUiState.loadResult) {
        Log.d("ItemScreen", "StartDate initialized = ${meciUiState.loadResult}");
        if (startDateInitialized) {
            return@LaunchedEffect
        }
        if (!(meciUiState.loadResult is Result.Loading)) {
            startDate = meciUiState.meci.startDate
            startDateInitialized = true
        }
    }

    var latitudeInitialized by remember { mutableStateOf(meciId == null) }
    LaunchedEffect(meciId, meciUiState.loadResult) {
        Log.d("ItemScreen", "Latitude initialized = ${meciUiState.loadResult}")
        if(latitudeInitialized) {
            return@LaunchedEffect
        }
        if(!(meciUiState.loadResult is Result.Loading)) {
            latitude = meciUiState.meci.latitude
            latitudeInitialized = true
        }
    }

    var longitudeInitialized by remember { mutableStateOf(meciId == null) }
    LaunchedEffect(meciId, meciUiState.loadResult) {
        Log.d("ItemScreen", "Longitude initialized = ${meciUiState.loadResult}")
        if(longitudeInitialized) {
            return@LaunchedEffect
        }
        if(!(meciUiState.loadResult is Result.Loading)) {
            longitude = meciUiState.meci.longitude
            longitudeInitialized = true
        }
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.meci)) },
                actions = {
                    Button(onClick = {
                        Log.d("MeciScreen", "save meci")
                        meciViewModel.saveOrUpdateMeci(name, pretBilet, hasStarted, startDate, latitude, longitude)
                    }) { Text("Save")}
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .fillMaxSize()
        ) {
            if(meciUiState.loadResult is Result.Loading) {
                CircularProgressIndicator()
                return@Scaffold
            }
            if(meciUiState.submitResult is Result.Loading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) { LinearProgressIndicator() }
            }
            if(meciUiState.loadResult is Result.Error) {
                Text(text = "Failed to load item - ${(meciUiState.loadResult as Result.Error).exception?.message}")
            }

            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = if (pretBilet == 0) "" else pretBilet.toString(),
                onValueChange = { input ->
                    pretBilet = input.toIntOrNull() ?: 0
                },
                label = { Text("Pret Bilet") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = hasStarted,
                    onCheckedChange = { hasStarted = it },
                )
                Text("Has Started")
            }
            Spacer(modifier = Modifier.height(16.dp))

            DateTimePicker(
                onDateTimeSelected = { dateTimeString ->
                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    val localDateTime = LocalDateTime.parse(dateTimeString, formatter)
                    val instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant()
                    startDate = Date.from(instant)
                }
            )

            MyMap(
                lat = latitude,
                lng = longitude,
                modifier = Modifier.fillMaxWidth(),
                onLocationChanged = {lat, lng ->
                    Log.d(TAG, "changing to ${lat} - ${lng}")
                    latitude = lat
                    longitude = lng
                }
            )
        }

        if(meciUiState.submitResult is Result.Error) {
            Text(
                text = "Failed to submiot meci - ${(meciUiState.submitResult as Result.Error).exception?.message}",
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun PreviewMeciScreen() {
    MeciScreen(meciId = "0", onClose = {})
}