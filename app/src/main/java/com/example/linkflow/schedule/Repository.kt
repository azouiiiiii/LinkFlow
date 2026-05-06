package com.example.linkflow.schedule

import kotlinx.coroutines.flow.Flow

class ScheduleRepository(
    private val dao: ScheduleDao
) {

    suspend fun insert(schedule: Schedule): Long {
        return dao.insert(schedule)
    }

    suspend fun delete(schedule: Schedule) {
        dao.delete(schedule)
    }

    // 🔥 直接暴露 Flow
    val allSchedules: Flow<List<Schedule>> = dao.getAll()
}