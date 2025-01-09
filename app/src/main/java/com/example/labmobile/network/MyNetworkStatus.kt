package com.example.labmobile.network

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.labmobile.meci.data.MeciDao
import com.example.labmobile.meci.data.MeciRepository
import kotlinx.coroutines.launch

class MyNetworkStatusViewModel(application: Application, meciDao: MeciDao, meciRepository: MeciRepository) : AndroidViewModel(application) {
    var uiState by mutableStateOf(false)
        private set
    var connectivityManager = ConnectivityManagerNetworkMonitor(getApplication(), meciDao, meciRepository)

    init {
        collectNetworkStatus()
    }

    private fun collectNetworkStatus() {
        viewModelScope.launch {
            connectivityManager.isOnline.collect {
                uiState = it;
            }
        }
    }

    companion object {
        fun Factory(application: Application, meciDao: MeciDao, meciRepository: MeciRepository) : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MyNetworkStatusViewModel(application, meciDao, meciRepository)
            }
        }
    }
}

@Composable
fun MyNetworkStatus(meciDao: MeciDao, meciRepository: MeciRepository) {
    val myNetworkStatusViewModel = viewModel<MyNetworkStatusViewModel>(
        factory = MyNetworkStatusViewModel.Factory(
            LocalContext.current.applicationContext as Application,
            meciDao,
            meciRepository
        )
    )

    val isOnline = myNetworkStatusViewModel.uiState

    val wifiIconColor = if (isOnline) Color.Green else Color.Red

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp) // Space between icon and text
    ) {
        Icon(
            imageVector = Icons.Rounded.Wifi,
            contentDescription = "Wi-Fi Status",
            tint = wifiIconColor
        )
    }
}