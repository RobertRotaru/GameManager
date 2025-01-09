package com.example.labmobile.meci.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MeciDao{
    @Query("SELECT * FROM Meciuri")
    fun getAll(): Flow<List<Meci>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meci: Meci)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(meciuri: List<Meci>)

    @Update
    suspend fun update(meci: Meci): Int

    @Query("DELETE FROM MECIURI WHERE _id = :id")
    suspend fun deleteById(id: String): Int

    @Query("DELETE FROM meciuri")
    suspend fun deleteAll()

    @Query("SELECT * FROM Meciuri")
    suspend fun getAllOnce(): List<Meci>

    @Query("SELECT * FROM meciuri WHERE isSynced = true")
    fun getSyncedMecis(): Flow<List<Meci>>
}