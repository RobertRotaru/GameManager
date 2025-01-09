package com.example.labmobile.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest.Builder
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.core.content.getSystemService
import com.example.labmobile.core.TAG
import com.example.labmobile.meci.data.MeciDao
import com.example.labmobile.meci.data.MeciRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch

class ConnectivityManagerNetworkMonitor(val context: Context, val meciDao: MeciDao, val meciRepository: MeciRepository) {
    private val networkMonitorScope = CoroutineScope(Dispatchers.IO)

    val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                channel.trySend(true)
                networkMonitorScope.launch {
                    syncMecisIfNeeded()
                }
            }

            override fun onLost(network: Network) {
                channel.trySend(false)
            }
        }

        val connectivityManager = context.getSystemService<ConnectivityManager>()

        connectivityManager?.registerNetworkCallback(
            Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .build(),
            callback
        )

        channel.trySend(connectivityManager.isCurrentlyConnected())

        awaitClose {
            connectivityManager?.unregisterNetworkCallback(callback)
        }
    }
        .conflate()

    @Suppress("DEPRECATION")
    private fun ConnectivityManager?.isCurrentlyConnected() = when (this) {
        null -> false
        else -> when {
            VERSION.SDK_INT >= VERSION_CODES.M ->
                activeNetwork
                    ?.let(::getNetworkCapabilities)
                    ?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    ?: false
            else -> activeNetworkInfo?.isConnected ?: false
        }
    }

    private suspend fun syncMecisIfNeeded() {
        Log.d(TAG, "syncMecisIfNeeded started")
        try {
            val mecis = meciDao.getAllOnce()
            Log.d(TAG, "Fetched mecics: $mecis")

            mecis.forEach {
                if (!it.isSynced) {
                    Log.d(TAG, "Syncing meci: $it")
                    it.isSynced = true
                    val savedMeci = meciRepository.save(it)
                    meciDao.update(it)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in syncMecisIfNeeded: ${e.message}", e)
        }
        Log.d(TAG, "syncMecisIfNeeded finished")
    }
}