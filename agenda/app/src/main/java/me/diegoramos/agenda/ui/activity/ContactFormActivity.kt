package me.diegoramos.agenda.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_contact_form.*
import me.diegoramos.agenda.Constants
import me.diegoramos.agenda.R
import me.diegoramos.agenda.dao.ContactDAO
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_form)
        setTitle(R.string.student_form_activity_title)

        handleFormMode()
        handleReceivedData()
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

    private fun resetFields() {
        activity_contact_form_name?.text = null
        activity_contact_form_email?.text = null
        activity_contact_form_phone?.text = null
    }

    fun handleSave(view: View) {
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

            Toast.makeText(this, "Contact ${activity_contact_form_name?.text.toString()} saved!", Toast.LENGTH_LONG).show()

            finish()
        } catch (ex: DuplicatedItemException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }

    }

    private fun handleUpdate(contact: Contact?) {
        ContactDAO.update(contact!!, this)
        setResult(Constants.updatedContactResultCode, prepareResult(contact))
    }

    private fun handleRegister(contact: Contact?) {
        ContactDAO.add(contact!!, this)
        setResult(Constants.createdContactResultCode, prepareResult(contact))
    }

    private fun prepareResult(contact: Contact): Intent {
        val intent = Intent()
        intent.putExtra(Constants.CONTACT_EXTRA_NAME, contact)

        return intent
    }

}

enum class FormMode {
    REGISTER, UPDATE
}