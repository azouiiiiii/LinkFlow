// 数据中转，后期功能优化友好

package com.example.linkflow.schedule

class ScheduleRepository (
    private val dao: ScheduleDao
){
    suspend fun insert(schedule: Schedule): Long {
        return dao.insert(schedule)
    }

    suspend fun getAll(): List<Schedule> {
        return dao.getAll()
    }
}