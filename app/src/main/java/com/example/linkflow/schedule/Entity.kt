// 实例化（数据类型定义，一般不做更改）

package com.example.linkflow.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.example.linkflow.AppType


enum class ReminderType {
    STATIC,
    DYNAMIC
}

@Entity(tableName = "schedule")
data class Schedule(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0, // 主键

    val date: String,
    val content: String,
    val triggerTime: Long,
    val appType: AppType,
    val extraData: String,
    val reminderType: ReminderType
)