package com.example.labmobile.meci.data

import android.util.Log
import com.example.labmobile.authRoute
import com.example.labmobile.core.TAG

import com.example.labmobile.meci.data.remote.ItemWsClient
import com.example.labmobile.meci.data.remote.MeciEvent
import com.example.labmobile.meci.data.remote.MeciService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import com.example.labmobile.core.data.remote.Api
import java.util.UUID

class MeciRepository (
    private val meciService: MeciService,
    private val meciWsClient: ItemWsClient,
    val meciDao: MeciDao
) {
    val meciStream by lazy { meciDao.getSyncedMecis() }

    init {
        Log.d(TAG, "init")
    }

    private fun getBearerToken() = "Bearer ${Api.tokenInterceptor.token}"

    suspend fun refresh() {
        Log.d(TAG, "refresh started")
        try {
            val meciuri = meciService.findAll(authorization = getBearerToken())
            meciDao.deleteAll()
            meciuri.forEach { meciDao.insert(it) }
            Log.d(TAG, "refresh succeeded")
        } catch (e: Exception) {
            Log.w(TAG, "refresh failed", e)
        }
    }

    suspend fun openWsClient() {
        Log.d(TAG, "openWsClient")
        withContext(Dispatchers.IO) {
            getMeciEvents().collect {
                Log.d(TAG, "Meci event collected $it")
                if(it.isSuccess) {
                    val meciEvent = it.getOrNull()
                    when(meciEvent?.type) {
                        "created" -> handleMeciCreated(meciEvent.payload)
                        "updated" -> handleMeciUpdated(meciEvent.payload)
                        "deleted" -> handleMeciDeleted(meciEvent.payload)
                    }
                }
            }
        }
    }

    suspend fun closeWsClient() {
        Log.d(TAG, "closeWsClient")
        withContext(Dispatchers.IO) {
            meciWsClient.closeSocket()
        }
    }

    suspend fun getMeciEvents(): Flow<Result<MeciEvent>> = callbackFlow {
        Log.d(TAG, "getMeciEvents started")
        meciWsClient.openSocket(
            onEvent = {
                Log.d(TAG, "onEvent $it")
                if(it != null) {
                    trySend(Result.success(it))
                }
            },
            onClosed = { close() },
            onFailure = { close() })
        awaitClose { meciWsClient.closeSocket() }
    }

    suspend fun update(meci: Meci): Meci {
        Log.d(TAG, "update $meci...")
        val updatedMeci =
            meciService.update(meciId = meci._id, meci = meci, authorization = getBearerToken())
        Log.d(TAG, "update $meci succeded")
        handleMeciUpdated(updatedMeci)
        return updatedMeci
    }

    suspend fun save(meci: Meci) : Meci {
        Log.d(TAG, "save $meci...")
        try {
            meci._id = UUID.randomUUID().toString()
            val createdMeci = meciService.create(meci = meci, authorization = getBearerToken())
            Log.d(TAG, "save $meci succeeded")
            handleMeciCreated(createdMeci)
            return createdMeci
        } catch (e: Exception) {
            Log.d(TAG, "save $meci failed... saving locally")
            meci.isSynced = false
            meciDao.insert(meci)
            return meci
        }
    }

    private suspend fun handleMeciDeleted(meci: Meci) {
        Log.d(TAG, "handleMeciDeleted - $meci")
    }

    private suspend fun handleMeciUpdated(meci: Meci) {
        Log.d(TAG, "handleMeciUpdated...")
        meciDao.update(meci)
    }

    private suspend fun handleMeciCreated(meci: Meci) {
        Log.d(TAG, "handleMeciCreated...")
        meciDao.insert(meci)
    }

    suspend fun deleteAll() {
        meciDao.deleteAll()
    }

    fun setToken(token: String) {
        meciWsClient.authorize(token)
    }
}