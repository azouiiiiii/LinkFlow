package com.example.linkflow.data

import androidx.room.TypeConverter
import com.example.linkflow.AppType
import com.example.linkflow.schedule.ReminderType

class Converters {

    @TypeConverter
    fun fromAppType(value: AppType): String {
        return value.name
    }

    @TypeConverter
    fun toAppType(value: String): AppType {
        return AppType.valueOf(value)
    }

    @TypeConverter
    fun fromReminderType(value: ReminderType): String {
        return value.name
    }

    @TypeConverter
    fun toReminderType(value: String): ReminderType {
        return ReminderType.valueOf(value)
    }
}