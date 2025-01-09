package com.example.labmobile.core.data.remote

import android.util.Log
import com.example.labmobile.core.TAG
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor : Interceptor {
    var token: String? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val originalUrl = original.url
        if(token == null){
            Log.d(TAG, "Token is null")
            return chain.proceed(original)
        }

        val requestBuilder = original.newBuilder()
            .addHeader("Authorization", "Beared $token")
            .url(originalUrl)
        val request = requestBuilder.build()
        Log.d(TAG, "Authorization Bearer added")
        return chain.proceed(request)
    }
}