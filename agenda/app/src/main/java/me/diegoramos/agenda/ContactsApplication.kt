package me.diegoramos.agenda

import android.app.Application
import androidx.room.Room
import me.diegoramos.agenda.database.ContactsDatabase
import me.diegoramos.agenda.database.Migrations

open class ContactsApplication: Application()  {

    companion object {
        lateinit var db: ContactsDatabase
    }

    override fun onCreate() {
        super.onCreate()

        db = Room.databaseBuilder(applicationContext, ContactsDatabase::class.java,
                Constants.DATABASE_NAME)
            .allowMainThreadQueries()
            .addMigrations(Migrations.MIGRATION_1_2, Migrations.MIGRATION_2_3)
            .build()
    }

}