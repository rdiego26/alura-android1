package me.diegoramos.agenda.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.diegoramos.agenda.database.converter.UUIDConverter
import me.diegoramos.agenda.database.dao.ContactDAO
import me.diegoramos.agenda.model.Contact

@Database(entities = [Contact::class], version = 2, exportSchema = false)
@TypeConverters(UUIDConverter::class)
abstract class ContactsDatabase : RoomDatabase() {
    abstract fun getContactDAO(): ContactDAO
}
