package me.diegoramos.agenda.database.converter

import androidx.room.TypeConverter
import java.util.*

class UUIDConverter {
    @TypeConverter
    fun fromString(value: String?): UUID? {
        return value?.let { UUID.fromString(it) }
    }

    @TypeConverter
    fun toString(value: UUID?): String? {
        return value?.let { value.toString() }
    }
}