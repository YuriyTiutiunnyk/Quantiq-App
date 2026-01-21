package com.example.quantiq.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.quantiq.data.notification.ItemNotificationConfigDao
import com.example.quantiq.data.notification.ItemNotificationConfigEntity

@Database(
    entities = [CounterEntity::class, ItemNotificationConfigEntity::class],
    version = 2,
    exportSchema = false
)
abstract class QuantiqDatabase : RoomDatabase() {
    abstract fun counterDao(): CounterDao
    abstract fun itemNotificationConfigDao(): ItemNotificationConfigDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(
                    \"\"\"
                    CREATE TABLE IF NOT EXISTS item_notification_configs (
                        itemId INTEGER NOT NULL,
                        enabled INTEGER NOT NULL,
                        title TEXT NOT NULL,
                        body TEXT NOT NULL,
                        scheduleType TEXT NOT NULL,
                        startAtEpochMillis INTEGER NOT NULL,
                        timeZoneId TEXT NOT NULL,
                        repeatType TEXT NOT NULL,
                        repeatIntervalMinutes INTEGER,
                        endAtEpochMillis INTEGER,
                        actionsJson TEXT NOT NULL,
                        PRIMARY KEY(itemId)
                    )
                    \"\"\".trimIndent()
                )
            }
        }
    }
}
