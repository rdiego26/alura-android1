package me.diegoramos.agenda.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.diegoramos.agenda.Constants
import me.diegoramos.agenda.ContactsApplication.Companion.db
import me.diegoramos.agenda.R
import me.diegoramos.agenda.asyncTask.CustomTask
import me.diegoramos.agenda.asyncTask.TaskDelegate
import me.diegoramos.agenda.model.Contact
import me.diegoramos.agenda.model.ContactAndPhones
import me.diegoramos.agenda.ui.adapter.ContactItemAdapter

class MainActivity : AppCompatActivity(), ContactItemAdapter.Events {

    lateinit var contactList: MutableList<ContactAndPhones>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.main_activity_title)

        configureRecyclerView()
        configureFABToForm()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (isCreateContactRequest(requestCode)) {
            if(isCanceledResult(resultCode)) {
                handleCanceledFeedback()
            } else if(isOkResult(resultCode) && hasContact(data)){
                handleAddedItemOnAdapter(data!!)
            }

        } else if(isUpdateContactRequest(requestCode)) {
            if(isCanceledResult(resultCode)) {
                handleCanceledFeedback()
            } else if(isOkResult(resultCode) && hasContact(data) && hasValidContactPosition(data)) {
                handleUpdatedItemOnAdapter(data!!)
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onItemClickListener(item: ContactAndPhones, position: Int) {
        val intent = Intent(applicationContext, ContactFormActivity::class.java)
        intent.putExtra(Constants.CONTACT_EXTRA_NAME, item)
        intent.putExtra(Constants.CONTACT_POSITION_EXTRA_NAME, position)
        startActivityForResult(intent, Constants.UPDATE_CONTACT_REQUEST_CODE)
    }

    override fun onItemLongClickListener(item: ContactAndPhones, position: Int): Boolean {
        dialogToRemove(item.contact, position)
        return false
    }

    private fun dialogToRemove(contact: Contact, position: Int) {
        val message = String.format(resources.getString(R.string.remove_student_question),
            contact.name)
        val btnConfirm = resources.getString(R.string.remove_student_confirmed)
        val btnCancel = resources.getString(R.string.remove_student_cancel)

        AlertDialog.Builder(this)
            .setTitle(R.string.remove_student_title)
            .setMessage(message)
            .setPositiveButton(btnConfirm) { _, _ ->
                CustomTask(object : TaskDelegate {
                    override fun background() {
                        db.getContactDAO().remove(contact)
                    }
                    override fun onFinish() = Unit
                }).execute()
                handleRemovedItemOnAdapter(position)
                Toast.makeText(this, String.format(
                    resources.getString(R.string.removed_student_message),
                    contact.name),
                    Toast.LENGTH_SHORT
                ).show()
                this.onResume()
            }
            .setNegativeButton(btnCancel) { _, _ ->
                Toast.makeText(this, resources.getString(R.string.removed_student_canceled),
                    Toast.LENGTH_SHORT
                ).show()
                this.onResume()
            }
            .show()
    }

    private fun handleCanceledFeedback() =
        Toast.makeText(applicationContext, R.string.operation_canceled, Toast.LENGTH_SHORT)
            .show()

    private fun isCanceledResult(resultCode: Int) = resultCode == Activity.RESULT_CANCELED

    private fun isOkResult(resultCode: Int) = resultCode == Activity.RESULT_OK

    private fun isCreateContactRequest(requestCode: Int) =
        requestCode == Constants.CREATE_CONTACT_REQUEST_CODE

    private fun configureRecyclerView() {

        CustomTask(object : TaskDelegate {
            override fun background() {
                contactList = mutableListOf()

                db.getContactDAO().getAll().map {
                    val phones = db.getPhoneDAO().getAllByContact(it.id)
                    contactList.add(ContactAndPhones(contact = it, phones = phones))
                }
            }

            override fun onFinish() {
                activity_main_contact_list.adapter = ContactItemAdapter(contactList, this@MainActivity)
            }
        }).execute()
    }

    private fun configureFABToForm() {
        activity_main_fab.setOnClickListener {
            val intent = Intent(applicationContext, ContactFormActivity::class.java)
            startActivityForResult(intent, Constants.CREATE_CONTACT_REQUEST_CODE)
        }
    }

    private fun hasContact(data: Intent?) =
        data?.hasExtra(Constants.CONTACT_EXTRA_NAME)!!

    private fun hasValidContactPosition(data: Intent?) =
        data?.hasExtra(Constants.CONTACT_POSITION_EXTRA_NAME)!! &&
        data.getIntExtra(Constants.CONTACT_POSITION_EXTRA_NAME,
            ContactFormActivity.INVALID_POSITION) != ContactFormActivity.INVALID_POSITION

    private fun isUpdateContactRequest(requestCode: Int) =
        requestCode == Constants.UPDATE_CONTACT_REQUEST_CODE


    private fun handleAddedItemOnAdapter(data: Intent) {
        val receivedContact: ContactAndPhones = data.getSerializableExtra(Constants.CONTACT_EXTRA_NAME) as ContactAndPhones
        ((this.activity_main_contact_list.adapter) as ContactItemAdapter).addContact(receivedContact)
        this.activity_main_contact_list.adapter?.notifyDataSetChanged()

        handleSavedContactFeedback(receivedContact)
    }

    private fun handleUpdatedItemOnAdapter(data: Intent) {
        val receivedContact: ContactAndPhones = data.getSerializableExtra(Constants.CONTACT_EXTRA_NAME) as ContactAndPhones
        val receivedPosition: Int = data.getIntExtra(Constants.CONTACT_POSITION_EXTRA_NAME, -1)
        ((this.activity_main_contact_list.adapter) as ContactItemAdapter).updateContact(receivedContact, receivedPosition)
        this.activity_main_contact_list.adapter?.notifyDataSetChanged()

        handleSavedContactFeedback(receivedContact)
    }

    private fun handleRemovedItemOnAdapter(position: Int) {
        ((this.activity_main_contact_list.adapter) as ContactItemAdapter).removeContact(position)
    }

    private fun handleSavedContactFeedback(item: ContactAndPhones) =
        Toast.makeText(this, "Contact ${item.contact.name} saved!", Toast.LENGTH_LONG).show()

}