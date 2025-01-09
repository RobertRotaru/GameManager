package com.example.labmobile.meci.data.remote

import android.util.Log
import com.example.labmobile.core.Api
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import com.example.labmobile.core.TAG
import okio.ByteString

class ItemWsClient(private val okHttpClient: OkHttpClient) {
    lateinit var webSocket: WebSocket

    suspend fun openSocket(
        onEvent: (meciEvent: MeciEvent?) -> Unit,
        onClosed: () -> Unit,
        onFailure: () -> Unit
    ) {
        withContext(Dispatchers.IO) {
            Log.d(TAG, "openSocket")
            val request = Request.Builder().url(Api.wsUrl).build()
            webSocket = okHttpClient.newWebSocket(
                request,
                MeciWebSocketListener(onEvent = onEvent, onClosed = onClosed, onFailure = onFailure)
            )
            okHttpClient.dispatcher.executorService.shutdown()
        }
    }

    fun closeSocket() {
        Log.d(TAG, "closeSocket")
        webSocket.close(1000, "")
    }

    inner class MeciWebSocketListener(
        private val onEvent: (meciEvent: MeciEvent?) -> Unit,
        private val onClosed: () -> Unit,
        private val onFailure: () -> Unit
    ) : WebSocketListener() {
        private val moshi = Moshi.Builder()
            .add(DateJsonAdapter())
            .build()
        private val itemEventJsonAdapter: JsonAdapter<MeciEvent> =
            moshi.adapter(MeciEvent::class.java)

        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, "onOpen")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, "onMessage string $text")
            val meciEvent = itemEventJsonAdapter.fromJson(text)
            onEvent(meciEvent)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d(TAG, "onMessage bytes $bytes")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {}

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, "onMessage bytes $code $reason")
            onClosed()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.d(TAG, "onMessage bytes $t")
            onFailure()
        }
    }

    fun authorize(token: String) {
        val auth = """
            {
              "type":"authorization",
              "payload":{
                "token": "$token"
              }
            }
        """.trimIndent()
        Log.d(TAG, "auth $auth")
        webSocket.send(auth)
    }
}