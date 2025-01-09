package com.example.labmobile

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.labmobile.auth.LoginScreen
import com.example.labmobile.core.data.UserPreferences
import com.example.labmobile.core.data.remote.Api
import com.example.labmobile.core.ui.UserPreferencesViewModel
import com.example.labmobile.meci.ui.meci.MeciScreen
import com.example.labmobile.meci.ui.meciuri.MeciuriScreen

val meciuriRoute = "meciuri"
val meciRoute = "meci"
val authRoute = "auth"

@Composable
fun MyAppNavHost() {
    val navController = rememberNavController()
    val onCloseMeci = {
        Log.d("MyAppNavHost", "navigate back to list")
        navController.popBackStack()
    }
    val userPreferencesModel =
        viewModel<UserPreferencesViewModel>(factory = UserPreferencesViewModel.Factory)
    val userPreferencesUiState by userPreferencesModel.uiState.collectAsStateWithLifecycle(
        initialValue = UserPreferences()
    )

    val myAppViewModel = viewModel<MyAppViewModel>(factory = MyAppViewModel.Factory)
    NavHost(
        navController = navController,
        startDestination = authRoute
    ) {
        composable(meciuriRoute) {
            MeciuriScreen(
                onMeciClick = { meciId ->
                    Log.d("myAppNavHost", "navigate to meci $meciId")
                    navController.navigate("$meciRoute/$meciId")
                },
                onAddMeci = {
                    Log.d("MyAppNavHost", "navigate to new meci")
                    navController.navigate("$meciRoute")
                },
                onLogout = {
                    Log.d("MyAppNavHost", "logout")
                    myAppViewModel.logout()
                    Api.tokenInterceptor.token = null
                    navController.navigate(authRoute) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(
            route = "$meciRoute/{id}",
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) {
            MeciScreen(
                meciId = it.arguments?.getString("id"),
                onClose = {onCloseMeci()}
            )
        }
        composable(
            route = "$meciRoute"
        ) {
            MeciScreen(
                meciId = null,
                onClose = {onCloseMeci()}
            )
        }
        composable(route = authRoute) {
            LoginScreen(
                onClose = {
                    Log.d("MyAppNavHost", "navigate to list")
                    navController.navigate(meciuriRoute)
                }
            )
        }
    }
    LaunchedEffect(userPreferencesUiState.token) {
        if(userPreferencesUiState.token.isNotEmpty()) {
            Log.d("MyAppNavHost", "Launched effect to anvigate to items")
            Api.tokenInterceptor.token = userPreferencesUiState.token
            myAppViewModel.setToken(userPreferencesUiState.token)
            navController.navigate(meciuriRoute) {
                popUpTo(0)
            }
        }
    }

}