package com.example.labmobile.meci.data.remote

import com.example.labmobile.meci.data.Meci

data class ApiResponse(
    val status: String?,
    val data: List<Meci>?
)