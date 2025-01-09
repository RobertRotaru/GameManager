package com.example.labmobile.meci.ui.meciuri

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.labmobile.MyApplication
import com.example.labmobile.core.TAG
import com.example.labmobile.meci.data.Meci
import com.example.labmobile.meci.data.MeciRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MeciuriViewModel(val meciuriRepository: MeciRepository) :
    ViewModel() {
        val uiState: Flow<List<Meci>> = meciuriRepository.meciStream

    init {
        Log.d(TAG, "init")
        loadMeciuri()
    }

    fun loadMeciuri() {
        Log.d(TAG, "loadItems...")
        viewModelScope.launch {
            meciuriRepository.refresh()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
                MeciuriViewModel(app.container.meciRepository)
            }
        }
    }
}