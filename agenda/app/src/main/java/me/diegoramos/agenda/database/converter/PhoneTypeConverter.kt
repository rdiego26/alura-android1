package me.diegoramos.agenda.database.converter

import androidx.room.TypeConverter
import me.diegoramos.agenda.model.PhoneType

class PhoneTypeConverter {
    @TypeConverter
    fun fromString(value: String?): PhoneType? {
        return value?.let { PhoneType.valueOf(it) }
    }

    @TypeConverter
    fun toString(value: PhoneType?): String? {
        return value?.let { value.toString() }
    }
}