package com.example.labmobile.core

import android.util.Log
import com.example.labmobile.meci.data.Meci
import com.example.labmobile.meci.data.remote.ApiResponse
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.reflect.Type

object Api {
    private val url = "10.0.2.2:3000"
    private val httpUrl = "http://$url/"
    val wsUrl = "ws://$url/"

    private var gson = GsonBuilder()
        .registerTypeAdapter(ApiResponse::class.java, ApiResponseDeserializer())
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl(httpUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    val okHttpClient = OkHttpClient.Builder()
        .build()
}

class ApiResponseDeserializer : JsonDeserializer<ApiResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ApiResponse {
        Log.d(TAG, "Received JSON: ${json?.toString()}")

        val jsonObject = json?.asJsonObject
        val status = jsonObject?.get("status")?.asString

        val data = if (jsonObject?.get("data")?.isJsonArray == true) {
            context?.deserialize<List<Meci>>(jsonObject["data"], List::class.java)
        } else {
            listOf()
        }

        return ApiResponse(status, data)
    }
}