package me.diegoramos.agenda.database.dao

import android.content.Context
import androidx.room.*
import me.diegoramos.agenda.R
import me.diegoramos.agenda.dao.ContactDAO
import me.diegoramos.agenda.model.BlankRequiredFieldException
import me.diegoramos.agenda.model.Contact
import me.diegoramos.agenda.model.DuplicatedItemException

@Dao
interface ContactDAO {

    @Insert
    fun save(contact: Contact)

    @Query("SELECT * FROM Contact C ORDER BY C.name DESC")
    fun getAll(): MutableList<Contact>

    @Delete
    fun remove(contact: Contact)
    
    @Update
    fun update(contact: Contact)

    companion object {
        fun validateAddOrUpdate(contact: Contact, context: Context) {
            val resources = context.resources
            val alreadyWithSameName = ContactDAO.getAll().any { it.name == contact.name && it.id != contact.id }
            if(alreadyWithSameName) {
                throw DuplicatedItemException(String.format( resources.getString(R.string.duplicated_item_by_name_message), contact.name))
            }

            val alreadyWithSameEmail = ContactDAO.getAll().any { it.email == contact.email && it.id != contact.id }
            if(alreadyWithSameEmail) {
                throw DuplicatedItemException(String.format( resources.getString(R.string.duplicated_item_by_email_message), contact.email))
            }

            when {
                contact.name.isBlank() -> {
                    throw BlankRequiredFieldException(resources.getString(R.string.contact_without_name_message))
                }
                contact.email.isBlank() -> {
                    throw BlankRequiredFieldException(resources.getString(R.string.contact_without_email_message))
                }
            }
        }
    }
}