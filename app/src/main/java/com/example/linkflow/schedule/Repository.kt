package com.example.linkflow.schedule

import kotlinx.coroutines.flow.Flow

class ScheduleRepository(
    private val dao: ScheduleDao
) {

    suspend fun insert(schedule: Schedule): Long {
        return dao.insert(schedule)
    }

    suspend fun update(schedule: Schedule) {
        dao.update(schedule)
    }

    suspend fun delete(schedule: Schedule) {
        dao.delete(schedule)
    }

    val allSchedules: Flow<List<Schedule>> = dao.getAll()
}
