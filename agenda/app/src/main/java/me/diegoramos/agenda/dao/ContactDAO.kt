package me.diegoramos.agenda.dao

import android.content.Context
import me.diegoramos.agenda.R
import me.diegoramos.agenda.model.BlankRequiredFieldException
import me.diegoramos.agenda.model.Contact
import me.diegoramos.agenda.model.DuplicatedItemException

object ContactDAO {

    var contacts: MutableList<Contact> = mutableListOf()

    fun add(contact: Contact, context: Context) {
        validateAddOrUpdate(contact, context)
        contacts.add(contact)
    }

    fun remove(contact: Contact) = contacts.remove(contact)

    fun update(contact: Contact, position: Int, context: Context) {
        validateAddOrUpdate(contact, context)
        contacts[position] = contact
    }

    fun getAll(): MutableList<Contact> {
        return contacts.toMutableList()
    }

    private fun validateAddOrUpdate(contact: Contact, context: Context) {
        val resources = context.resources
        val alreadyWithSameName = contacts.any { it.name == contact.name && it.id != contact.id }
        if(alreadyWithSameName) {
            throw DuplicatedItemException(String.format( resources.getString(R.string.duplicated_item_by_name_message), contact.name))
        }

        val alreadyWithSameEmail = contacts.any { it.email == contact.email && it.id != contact.id }
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

