package com.example.quantiq.data

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Represents CounterEntity.
 */
@Entity(tableName = "counters")
data class CounterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val value: Int = 0,
    val step: Int = 1,
    val isLocked: Boolean = false // For future PRO logic
)

/**
 * Defines the CounterDao contract.
 */
@Dao
interface CounterDao {
    @Query("SELECT * FROM counters ORDER BY id DESC")
    fun getAllCounters(): Flow<List<CounterEntity>>

    @Query("SELECT * FROM counters WHERE id = :id")
    fun observeCounterById(id: Long): Flow<CounterEntity?>

    @Query("SELECT * FROM counters WHERE id = :id")
    suspend fun getCounterById(id: Long): CounterEntity?

    @Insert
    suspend fun insert(counter: CounterEntity)

    @Update
    suspend fun update(counter: CounterEntity)

    @Query("DELETE FROM counters WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("UPDATE counters SET value = 0 WHERE id = :id")
    suspend fun resetCounter(id: Long)
}
