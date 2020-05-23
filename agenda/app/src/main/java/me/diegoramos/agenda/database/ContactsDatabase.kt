package me.diegoramos.agenda.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import me.diegoramos.agenda.Constants
import me.diegoramos.agenda.database.converter.UUIDConverter
import me.diegoramos.agenda.database.dao.ContactDAO
import me.diegoramos.agenda.model.Contact

@Database(entities = [Contact::class], version = 1, exportSchema = false)
@TypeConverters(UUIDConverter::class)
abstract class ContactsDatabase : RoomDatabase() {
    abstract fun getContactDAO(): ContactDAO

    companion object {
        private var INSTANCE: ContactsDatabase? = null

        fun getAppDataBase(context: Context): ContactsDatabase {
            if (INSTANCE == null) {
                synchronized(ContactsDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        ContactsDatabase::class.java,
                        Constants.DATABASE_NAME)
                        .build()
                }
            }
            return INSTANCE as ContactsDatabase
        }

        fun destroyDataBase() {
            INSTANCE = null
        }
    }
}
