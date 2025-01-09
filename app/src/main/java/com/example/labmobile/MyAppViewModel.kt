package com.example.labmobile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.labmobile.core.TAG
import com.example.labmobile.core.data.UserPreferences
import com.example.labmobile.core.data.UserPreferencesRepository
import com.example.labmobile.meci.data.MeciRepository
import kotlinx.coroutines.launch

class MyAppViewModel(
    private val userPreferencesRepository: UserPreferencesRepository,
    private val meciRepository: MeciRepository
) : ViewModel() {
    init {
        Log.d(TAG, "init")
    }

    fun logout() {
        viewModelScope.launch {
            meciRepository.deleteAll()
            userPreferencesRepository.save(UserPreferences())
        }
    }

    fun setToken(token: String) {
        meciRepository.setToken(token)
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
                MyAppViewModel (
                    app.container.userPreferencesReposiotry,
                    app.container.meciRepository
                )
            }
        }
    }
}