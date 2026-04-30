// 数据操作（增删改查，只规定动作，不参杂逻辑）

package com.example.linkflow.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ScheduleDao {

    @Insert
    suspend fun insert(schedule: Schedule) : Long

    @Query("SELECT * FROM schedule")
    suspend fun getAll(): List<Schedule>
    @Query("SELECT * FROM schedule WHERE id = :id")
    fun getScheduleById(id: Int): Schedule

    @Delete
    suspend fun delete(schedule: Schedule)

    @Update
    suspend fun update(schedule: Schedule)
}