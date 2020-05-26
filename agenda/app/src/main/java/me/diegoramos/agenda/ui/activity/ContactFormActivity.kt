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
    private var receivedContact: ContactAndPhones? = null
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
                receivedData.getSerializableExtra(Constants.CONTACT_EXTRA_NAME) as ContactAndPhones
            receivedContactPosition = receivedData.getIntExtra(Constants.CONTACT_POSITION_EXTRA_NAME,
                INVALID_POSITION)

            CustomTask(object : TaskDelegate {
                override fun background() {
                    val phone = receivedContact?.contact?.id?.let {
                        db.getPhoneDAO().getAllByContact(it)
                            .firstOrNull { it.type == PhoneType.HOME }?.number
                    }
                    val mobile = receivedContact?.contact?.id?.let {
                        db.getPhoneDAO().getAllByContact(it)
                            .firstOrNull { it.type == PhoneType.MOBILE }?.number
                    }
                    activity_contact_form_name.setText(receivedContact?.contact?.name)
                    activity_contact_form_last_name.setText(receivedContact?.contact?.lastName)
                    activity_contact_form_email.setText(receivedContact?.contact?.email)
                    activity_contact_form_phone.setText(phone)
                    activity_contact_form_mobile_phone.setText(mobile)

                    mode = FormMode.UPDATE
                }
                override fun onFinish() = Unit
            }).execute()
        }
    }

    private fun hasReceivedContact(intent: Intent) =
        intent.hasExtra(Constants.CONTACT_EXTRA_NAME)

    private fun bindSaveButton() {
        activity_contact_form_button.setOnClickListener {
            val contact: Contact?
            val contactAndPhones: ContactAndPhones?

            try {
                if (mode == FormMode.REGISTER) {
                    contact = Contact(name = activity_contact_form_name?.text.toString(),
                        lastName = activity_contact_form_last_name?.text.toString(),
                        email = activity_contact_form_email?.text.toString())

                    val phone = Phone(type = PhoneType.HOME, number = activity_contact_form_phone?.text.toString(),
                        contactId = contact.id)
                    val mobile = Phone(type = PhoneType.MOBILE, number = activity_contact_form_mobile_phone?.text.toString(),
                        contactId = contact.id)

                    contactAndPhones = ContactAndPhones(contact = contact, phones = listOf(phone, mobile))
                    handleRegister(contactAndPhones)
                } else {
                    val phone = Phone(type = PhoneType.HOME, number = activity_contact_form_phone?.text.toString(),
                        contactId = receivedContact?.contact?.id!!)
                    val mobile = Phone(type = PhoneType.MOBILE, number = activity_contact_form_mobile_phone?.text.toString(),
                        contactId = receivedContact?.contact?.id!!)

                    contact = Contact(id = receivedContact?.contact!!.id, name = activity_contact_form_name?.text.toString(),
                        lastName = activity_contact_form_last_name?.text.toString(),
                        email = activity_contact_form_email?.text.toString())
                    contactAndPhones = ContactAndPhones(contact = contact, phones = listOf(phone, mobile))
                    handleUpdate(contactAndPhones)
                }

                finish()
            } catch (ex: DuplicatedItemException) {
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
            } catch (ex: BlankRequiredFieldException) {
                Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun handleUpdate(item: ContactAndPhones) {
        validateAddOrUpdate(item)

        CustomTask(object : TaskDelegate {
            override fun background() {
                val phone = db.getPhoneDAO().getAllByContact(item.contact.id)
                    .firstOrNull { it.type == PhoneType.HOME }
                val mobilePhone = db.getPhoneDAO().getAllByContact(item.contact.id)
                    .firstOrNull { it.type == PhoneType.MOBILE }

                db.getContactDAO().update(item.contact)

                if (phone == null) {
                    db.getPhoneDAO().save(Phone(number = activity_contact_form_phone?.text.toString(),
                        type = PhoneType.HOME, contactId = item.contact.id))
                } else {
                    db.getPhoneDAO().update(Phone(number = activity_contact_form_phone?.text.toString(),
                        contactId = item.contact.id, id = phone.id))
                }

                if (mobilePhone == null) {
                    db.getPhoneDAO().save(Phone(number = activity_contact_form_mobile_phone?.text.toString(),
                        type = PhoneType.MOBILE, contactId = item.contact.id))
                } else {
                    db.getPhoneDAO().update(Phone(number = activity_contact_form_mobile_phone?.text.toString(),
                        contactId = item.contact.id, id = mobilePhone.id))
                }

            }
            override fun onFinish() = Unit
        }).execute()

        setResult(Activity.RESULT_OK, prepareResult(item))

    }

    private fun handleRegister(item: ContactAndPhones) {
        validateAddOrUpdate(item)

        CustomTask(object : TaskDelegate {
            override fun background() {
                db.getContactDAO().save(item.contact)
                db.getPhoneDAO().save(Phone(number = activity_contact_form_phone?.text.toString(),
                    type = PhoneType.HOME, contactId = item.contact.id))
                db.getPhoneDAO().save(Phone(number = activity_contact_form_mobile_phone?.text.toString(),
                    type = PhoneType.MOBILE, contactId = item.contact.id))
            }
            override fun onFinish() = Unit
        }).execute()

        setResult(Activity.RESULT_OK, prepareResult(item))

    }

    private fun prepareResult(item: ContactAndPhones): Intent {
        val intent = Intent()
        intent.putExtra(Constants.CONTACT_EXTRA_NAME, item)
        intent.putExtra(Constants.CONTACT_POSITION_EXTRA_NAME, receivedContactPosition)

        return intent
    }

    private fun validateAddOrUpdate(item: ContactAndPhones) {
        val resources = applicationContext.resources
        lateinit var allData: MutableList<Contact>

        CustomTask(object : TaskDelegate {
            override fun background() {
                allData = db.getContactDAO().getAll()
            }
            override fun onFinish() {
                val alreadyWithSameName = allData.any { item.contact.lastName == it.lastName && item.contact.id != it.id }
                if(alreadyWithSameName) {
                    throw DuplicatedItemException(String.format( resources.getString(R.string.duplicated_item_by_name_message), item.contact.name))
                }

                val alreadyWithSameEmail = allData.any { it.email == item.contact.email && it.id != item.contact.id }
                if(alreadyWithSameEmail) {
                    throw DuplicatedItemException(String.format( resources.getString(R.string.duplicated_item_by_email_message), item.contact.email))
                }

                when {
                    item.contact.name.isBlank() -> {
                        throw BlankRequiredFieldException(resources.getString(R.string.contact_without_name_message))
                    }
                    item.contact.email.isBlank() -> {
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