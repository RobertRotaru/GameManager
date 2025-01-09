package com.example.labmobile

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.labmobile.meci.data.Converter
import com.example.labmobile.meci.data.Meci
import com.example.labmobile.meci.data.MeciDao

@Database(entities = arrayOf(Meci::class), version = 1, exportSchema = false)
@TypeConverters(Converter::class)
abstract class MyDB : RoomDatabase() {
    abstract fun meciDao(): MeciDao

    companion object {
        @Volatile
        private var INSTANCE: MyDB? = null

        fun getDB(context: Context): MyDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    MyDB::class.java,
                    "app_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}