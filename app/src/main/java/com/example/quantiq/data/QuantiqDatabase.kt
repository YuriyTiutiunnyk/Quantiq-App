package com.example.quantiq.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [CounterEntity::class], version = 1, exportSchema = false)
abstract class QuantiqDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
}
