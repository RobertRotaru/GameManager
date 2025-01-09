package com.example.labmobile.meci.ui.meciuri

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labmobile.R
import com.example.labmobile.jobs.MyJobs
import com.example.labmobile.network.MyNetworkStatus
import com.example.labmobile.notifications.MyNotifications

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeciuriScreen(
    onMeciClick: (id: String?) -> Unit,
    onAddMeci: () -> Unit,
    onLogout: () -> Unit
) {
    Log.d("MeciuriScreen", "recompose")
    val meciuriViewModel = viewModel<MeciuriViewModel>(factory = MeciuriViewModel.Factory)
    val meciuriUiState by meciuriViewModel.uiState.collectAsStateWithLifecycle(
        initialValue = listOf()
    )

    var selectedTabIndex by remember { mutableStateOf(0) } // State for selected tab index

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.meciuri)) },
                navigationIcon = {MyNetworkStatus(meciuriViewModel.meciuriRepository.meciDao, meciuriViewModel.meciuriRepository) },
                actions = {
                    Button(onClick = onLogout) { Text("Logout") }
                }
            )
        },
        floatingActionButton = {
            if (selectedTabIndex == 0) { // Show FAB only for the first tab
                FloatingActionButton(
                    onClick = {
                        Log.d("MeciuriScreen", "add")
                        onAddMeci()
                    }
                ) {
                    Icon(Icons.Rounded.Add, "Add")
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Tabs Row
            TabRow(
                selectedTabIndex = selectedTabIndex
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Meciuri") }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("My Jobs") }
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = {selectedTabIndex = 2},
                    text = {Text("Notifications")}
                )
            }

            // Tab content
            when (selectedTabIndex) {
                0 -> MeciList(
                    meciList = meciuriUiState,
                    onMeciClick = onMeciClick,
                    modifier = Modifier.fillMaxSize()
                )
                1 -> MyJobs()
                2 -> MyNotifications()
            }
        }
    }
}

@Preview
@Composable
fun PreviewMeciuriScreen() {
    MeciuriScreen(onMeciClick = {}, onAddMeci = {}, onLogout = {})
}