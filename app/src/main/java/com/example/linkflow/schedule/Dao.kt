package com.example.linkflow.schedule

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Insert
    suspend fun insert(schedule: Schedule): Long

    // 🔥 改这里：Flow + 排序
    @Query("SELECT * FROM schedule ORDER BY triggerTime ASC")
    fun getAll(): Flow<List<Schedule>>

    @Query("SELECT * FROM schedule WHERE id = :id")
    suspend fun getScheduleById(id: Int): Schedule?

    @Delete
    suspend fun delete(schedule: Schedule)

    @Update
    suspend fun update(schedule: Schedule)
}