package me.diegoramos.agenda.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_contact_form.*
import me.diegoramos.agenda.Constants
import me.diegoramos.agenda.ContactsApplication
import me.diegoramos.agenda.R
import me.diegoramos.agenda.database.dao.ContactDAO
import me.diegoramos.agenda.model.BlankRequiredFieldException
import me.diegoramos.agenda.model.Contact
import me.diegoramos.agenda.model.DuplicatedItemException

class ContactFormActivity : AppCompatActivity() {

    companion object {
        const val INVALID_POSITION: Int = -1
    }

    private var mode: FormMode = FormMode.REGISTER
    private var receivedContact: Contact? = null
    private var receivedContactPosition: Int = INVALID_POSITION
    private var isEditMode: Boolean = false
    private var dao: ContactDAO? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_form)
        setTitle(R.string.student_form_activity_title)

        fetchDAO()
        bindSaveButton()
        handleFormMode()
        handleReceivedData()
    }

    private fun fetchDAO() {
        dao = ContactsApplication.db.getContactDAO()
    }

    private fun handleFormMode() {
        isEditMode = hasReceivedContact(intent)
    }

    private fun handleReceivedData() {
        if (isEditMode) {
            val receivedData = intent
            receivedContact =
                receivedData.getSerializableExtra(Constants.CONTACT_EXTRA_NAME) as Contact
            receivedContactPosition = receivedData.getIntExtra(Constants.CONTACT_POSITION_EXTRA_NAME,
                INVALID_POSITION)

            handleEdit(receivedContact as Contact)

            activity_contact_form_name.setText(receivedContact?.name)
            activity_contact_form_email.setText(receivedContact?.email)
            activity_contact_form_phone.setText(receivedContact?.phone)
        }
    }

    private fun hasReceivedContact(intent: Intent) =
        intent.hasExtra(Constants.CONTACT_EXTRA_NAME)

    private fun handleEdit(contact: Contact) {
        setTitle(R.string.student_form_activity_edit_title)
        activity_contact_form_name?.setText(contact.name)
        activity_contact_form_email?.setText(contact.email)
        activity_contact_form_phone?.setText(contact.phone)
        mode = FormMode.UPDATE
    }

    private fun bindSaveButton() {
        activity_contact_form_button.setOnClickListener {
            val contact: Contact?

            try {
                if (mode == FormMode.REGISTER) {
                    contact = Contact(name = activity_contact_form_name?.text.toString(),
                        email = activity_contact_form_email?.text.toString(),
                        phone = activity_contact_form_phone?.text.toString())
                    handleRegister(contact)
                } else {
                    contact = Contact(id = receivedContact!!.id, name = activity_contact_form_name?.text.toString(),
                        email = activity_contact_form_email?.text.toString(),
                        phone = activity_contact_form_phone?.text.toString())
                    handleUpdate(contact)
                }

                finish()
            } catch (ex: DuplicatedItemException) {
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
            } catch (ex: BlankRequiredFieldException) {
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleUpdate(contact: Contact?) {
        validateAddOrUpdate(contact!!)
        dao?.update(contact)
        setResult(Activity.RESULT_OK, prepareResult(contact))
    }

    private fun handleRegister(contact: Contact?) {
        validateAddOrUpdate(contact!!)
        dao?.save(contact)
        setResult(Activity.RESULT_OK, prepareResult(contact))
    }

    private fun prepareResult(contact: Contact): Intent {
        val intent = Intent()
        intent.putExtra(Constants.CONTACT_EXTRA_NAME, contact)
        intent.putExtra(Constants.CONTACT_POSITION_EXTRA_NAME, receivedContactPosition)

        return intent
    }

    private fun validateAddOrUpdate(contact: Contact) {
        val resources = applicationContext.resources
        val alreadyWithSameName = me.diegoramos.agenda.dao.ContactDAO.getAll().any { it.name == contact.name && it.id != contact.id }
        if(alreadyWithSameName) {
            throw DuplicatedItemException(String.format( resources.getString(R.string.duplicated_item_by_name_message), contact.name))
        }

        val alreadyWithSameEmail = me.diegoramos.agenda.dao.ContactDAO.getAll().any { it.email == contact.email && it.id != contact.id }
        if(alreadyWithSameEmail) {
            throw DuplicatedItemException(String.format( resources.getString(R.string.duplicated_item_by_email_message), contact.email))
        }

        val alreadyWithSamePhone = me.diegoramos.agenda.dao.ContactDAO.getAll().any { it.phone == contact.phone && it.id != contact.id }
        if(alreadyWithSamePhone) {
            throw DuplicatedItemException(String.format( resources.getString(R.string.duplicated_item_by_phone_message), contact.phone))
        }

        when {
            contact.name.isBlank() -> {
                throw BlankRequiredFieldException(resources.getString(R.string.contact_without_name_message))
            }
            contact.email.isBlank() -> {
                throw BlankRequiredFieldException(resources.getString(R.string.contact_without_email_message))
            }
            contact.phone.isBlank() -> {
                throw BlankRequiredFieldException(resources.getString(R.string.contact_without_phone_message))
            }
        }
    }

}

enum class FormMode {
    REGISTER, UPDATE
}