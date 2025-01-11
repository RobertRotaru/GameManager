package com.example.labmobile.meci.ui.meci

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.labmobile.MyApplication
import com.example.labmobile.core.Result
import com.example.labmobile.meci.data.Meci
import com.example.labmobile.meci.data.MeciRepository
import kotlinx.coroutines.launch
import java.util.Date
import com.example.labmobile.core.TAG

data class MeciUiState (
    val meciId: String? = null,
    val meci: Meci = Meci(),
    var loadResult: Result<Meci>? = null,
    var submitResult: Result<Meci>? = null
)

class MeciViewModel(private val meciId: String?, private val meciRepository: MeciRepository) :
        ViewModel() {
            var uiState: MeciUiState by mutableStateOf(MeciUiState(loadResult = Result.Loading))
                private set

            init {
                Log.d(TAG, "init")
                if(meciId != null) {
                    loadMeci()
                } else {
                    uiState = uiState.copy(loadResult = Result.Success(Meci()))
                }
            }

            fun loadMeci() {
                viewModelScope.launch {
                    meciRepository.meciStream.collect { meciuri ->
                        if(!(uiState.loadResult is Result.Loading)) {
                            return@collect
                        }
                        val meci = meciuri.find { it._id == meciId }?: Meci()
                        uiState = uiState.copy(meci = meci, loadResult = Result.Success(meci))
                    }
                }
            }

            fun saveOrUpdateMeci(name: String, pretBilet: Int, hasStarted: Boolean, startDate: Date, latitude: Double, longitude: Double) {
                viewModelScope.launch {
                    Log.d(TAG, "saveOrUpdateMeci...")
                    try {
                        uiState = uiState.copy(submitResult = Result.Loading)
                        val meci = uiState.meci.copy(name = name, hasStarted = hasStarted, pretBilet = pretBilet, startDate = startDate, latitude = latitude, longitude = longitude)
                        val savedMeci: Meci
                        if(meciId == null) {
                            savedMeci = meciRepository.save(meci)
                        } else {
                            savedMeci = meciRepository.update(meci)
                        }
                        Log.d("TAG", "saveOrUpdateMeci succeeded")
                        uiState = uiState.copy(submitResult = Result.Success(savedMeci))
                    } catch (e: Exception) {
                        Log.d(TAG, "saveOrUpdateItem failed")
                        uiState = uiState.copy(submitResult = Result.Error(e))
                    }
                }
            }

            companion object {
                fun Factory(meciId: String?): ViewModelProvider.Factory = viewModelFactory {
                    initializer {
                        val app =
                            (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MyApplication)
                        MeciViewModel(meciId, app.container.meciRepository)
                    }
                }
            }
        }