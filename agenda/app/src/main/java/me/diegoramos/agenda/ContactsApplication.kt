package me.diegoramos.agenda

import android.app.Application
import androidx.room.Room
import me.diegoramos.agenda.database.ContactsDatabase

open class ContactsApplication: Application()  {

    companion object {
        lateinit var db: ContactsDatabase
    }

    override fun onCreate() {
        super.onCreate()
        setupRoom()
    }

    private fun setupRoom() {
        db = Room.databaseBuilder(applicationContext, ContactsDatabase::class.java,Constants.DATABASE_NAME)
            .build()
    }

}