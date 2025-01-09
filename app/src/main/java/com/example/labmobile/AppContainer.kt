package com.example.labmobile

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import com.example.labmobile.auth.data.AuthRepository
import com.example.labmobile.auth.data.remote.AuthDataSource
import com.example.labmobile.core.Api
import com.example.labmobile.core.TAG
import com.example.labmobile.core.data.UserPreferencesRepository
import com.example.labmobile.meci.data.MeciRepository
import com.example.labmobile.meci.data.remote.ItemWsClient
import com.example.labmobile.meci.data.remote.MeciService

val Context.userPreferencesDataStore by preferencesDataStore (
    name = "user_preferences"
)

class AppContainer(val context: Context) {
    init {
        Log.d(TAG, "init")
    }

    private val meciService: MeciService = Api.retrofit.create(MeciService::class.java)
    private val meciWsClient: ItemWsClient = ItemWsClient(Api.okHttpClient)
    private val authDataSource: AuthDataSource = AuthDataSource()

    private val database: MyDB by lazy {
        MyDB.getDB(context)
    }

    val meciRepository: MeciRepository by lazy {
        MeciRepository(meciService, meciWsClient, database.meciDao())
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(authDataSource)
    }

    val userPreferencesReposiotry: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context.userPreferencesDataStore)
    }
}