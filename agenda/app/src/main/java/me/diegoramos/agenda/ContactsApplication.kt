package me.diegoramos.agenda

import android.app.Application
import me.diegoramos.agenda.database.ContactsDatabase

open class ContactsApplication: Application()  {

    companion object {
        lateinit var db: ContactsDatabase
    }

}