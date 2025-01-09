package com.example.labmobile.meci.data.remote

import android.util.Log
import com.example.labmobile.core.TAG
import com.example.labmobile.meci.data.Meci
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MeciService {
    @GET("/api/meciuri")
    suspend fun findAll(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int = 0,
        @Query("pageSize") pageSize: Int = 15,
        @Query("hasStarted") hasStarted: Boolean = false): List<Meci>

    @GET("/api/meci/{id}")
    suspend fun findOne(
        @Header("Authorization") authorization: String,
        @Path("id") meciId: String?
    ) : Meci

    @Headers("Content-Type: application/json")
    @POST("/api/meci")
    suspend fun create(@Header("Authorization") authorization: String, @Body meci: Meci): Meci

    @Headers("Content-Type: application/json")
    @PUT("/api/meci/{id}")
    suspend fun update(
        @Header("Authorization") authorization: String,
        @Path("id") meciId: String?,
        @Body meci: Meci
    ): Meci
}