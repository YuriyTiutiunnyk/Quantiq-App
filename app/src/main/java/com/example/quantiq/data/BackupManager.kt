package com.example.quantiq.data

import android.content.Context
import android.net.Uri
import com.example.quantiq.domain.model.Counter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

data class BackupData(
    val version: Int = 1,
    val timestamp: Long,
    val counters: List<Counter>
)

class BackupManager(private val context: Context) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun exportData(uri: Uri, counters: List<Counter>): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val backup = BackupData(
                timestamp = System.currentTimeMillis(),
                counters = counters
            )
            val json = gson.toJson(backup)
            
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(json.toByteArray())
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun importData(uri: Uri): Result<List<Counter>> = withContext(Dispatchers.IO) {
        try {
            val stringBuilder = StringBuilder()
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BufferedReader(InputStreamReader(inputStream)).use { reader ->
                    var line: String? = reader.readLine()
                    while (line != null) {
                        stringBuilder.append(line)
                        line = reader.readLine()
                    }
                }
            }
            
            val json = stringBuilder.toString()
            val backup = gson.fromJson(json, BackupData::class.java)
            
            // Simple validation
            if (backup.version > 1) {
                // Handle version migration if needed
            }
            
            Result.success(backup.counters)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
