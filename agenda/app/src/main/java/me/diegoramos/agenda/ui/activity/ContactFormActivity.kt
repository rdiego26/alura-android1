package me.diegoramos.agenda.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_contact_form.*
import me.diegoramos.agenda.Constants
import me.diegoramos.agenda.ContactsApplication.Companion.db
import me.diegoramos.agenda.R
import me.diegoramos.agenda.asyncTask.CustomTask
import me.diegoramos.agenda.asyncTask.TaskDelegate
import me.diegoramos.agenda.model.*

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

        bindSaveButton()
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

            CustomTask(object : TaskDelegate {
                override fun background() {
                    val phone = db.getPhoneDAO().getAllByContact(receivedContact?.id!!)
                        .firstOrNull { it.type == PhoneType.HOME }?.number
                    val mobile = db.getPhoneDAO().getAllByContact(receivedContact?.id!!)
                        .firstOrNull { it.type == PhoneType.MOBILE }?.number

                    activity_contact_form_name.setText(receivedContact?.name)
                    activity_contact_form_last_name.setText(receivedContact?.lastName)
                    activity_contact_form_email.setText(receivedContact?.email)
                    activity_contact_form_phone.setText(phone)
                    activity_contact_form_mobile_phone.setText(mobile)

                    mode = FormMode.UPDATE
                }
            }).execute()
        }
    }

    private fun hasReceivedContact(intent: Intent) =
        intent.hasExtra(Constants.CONTACT_EXTRA_NAME)

    private fun bindSaveButton() {
        activity_contact_form_button.setOnClickListener {
            val contact: Contact?

            try {
                if (mode == FormMode.REGISTER) {
                    contact = Contact(name = activity_contact_form_name?.text.toString(),
                        lastName = activity_contact_form_last_name?.text.toString(),
                        email = activity_contact_form_email?.text.toString())
                    handleRegister(contact)
                } else {
                    contact = Contact(id = receivedContact!!.id, name = activity_contact_form_name?.text.toString(),
                        lastName = activity_contact_form_last_name?.text.toString(),
                        email = activity_contact_form_email?.text.toString())
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

        CustomTask(object : TaskDelegate {
            override fun background() {
                val phone = db.getPhoneDAO().getAllByContact(contact.id)
                    .firstOrNull { it.type == PhoneType.HOME }
                val mobilePhone = db.getPhoneDAO().getAllByContact(contact.id)
                    .firstOrNull { it.type == PhoneType.MOBILE }

                db.getContactDAO().update(contact)

                if (phone == null) {
                    db.getPhoneDAO().save(Phone(number = activity_contact_form_phone?.text.toString(),
                        type = PhoneType.HOME, contactId = contact.id))
                } else {
                    db.getPhoneDAO().update(Phone(number = activity_contact_form_phone?.text.toString(),
                        contactId = contact.id, id = phone.id))
                }

                if (mobilePhone == null) {
                    db.getPhoneDAO().save(Phone(number = activity_contact_form_mobile_phone?.text.toString(),
                        type = PhoneType.MOBILE, contactId = contact.id))
                } else {
                    db.getPhoneDAO().update(Phone(number = activity_contact_form_mobile_phone?.text.toString(),
                        contactId = contact.id, id = mobilePhone.id))
                }

            }
        }).execute()

        setResult(Activity.RESULT_OK, prepareResult(contact))

    }

    private fun handleRegister(contact: Contact?) {
        validateAddOrUpdate(contact!!)

        CustomTask(object : TaskDelegate {
            override fun background() {
                db.getContactDAO().save(contact)
                db.getPhoneDAO().save(Phone(number = activity_contact_form_phone?.text.toString(),
                    type = PhoneType.HOME, contactId = contact.id))
                db.getPhoneDAO().save(Phone(number = activity_contact_form_mobile_phone?.text.toString(),
                    type = PhoneType.MOBILE, contactId = contact.id))
            }
        }).execute()

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

        CustomTask(object : TaskDelegate {
            override fun background() {

                val allData = db.getContactDAO().getAll()

                val alreadyWithSameName = allData.any { it.lastName == contact.lastName && it.id != contact.id }
                if(alreadyWithSameName) {
                    throw DuplicatedItemException(String.format( resources.getString(R.string.duplicated_item_by_name_message), contact.name))
                }

                val alreadyWithSameEmail = allData.any { it.email == contact.email && it.id != contact.id }
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
        }).execute()

    }

}

enum class FormMode {
    REGISTER, UPDATE
}