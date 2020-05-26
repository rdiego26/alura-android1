package me.diegoramos.agenda.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.diegoramos.agenda.database.converter.PhoneTypeConverter
import me.diegoramos.agenda.database.dao.ContactDAO
import me.diegoramos.agenda.database.dao.PhoneDAO
import me.diegoramos.agenda.model.Contact
import me.diegoramos.agenda.model.Phone

@Database(entities = [Contact::class, Phone::class], version = 4, exportSchema = false)
@TypeConverters(PhoneTypeConverter::class)
abstract class ContactsDatabase : RoomDatabase() {
    abstract fun getContactDAO(): ContactDAO
    abstract fun getPhoneDAO(): PhoneDAO
}
