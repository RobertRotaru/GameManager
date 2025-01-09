package com.example.labmobile.meci.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.Date

@Entity(tableName = "meciuri")
data class Meci(
    @PrimaryKey var _id: String = "",
    val name: String = "",
    val pretBilet: Int = 0,
    val hasStarted: Boolean = false,
    val startDate: Date = Date.from(Instant.now()),
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    var isSynced: Boolean = true) {
}