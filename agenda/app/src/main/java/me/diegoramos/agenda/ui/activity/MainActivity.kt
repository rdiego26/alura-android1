package me.diegoramos.agenda.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import me.diegoramos.agenda.Constants
import me.diegoramos.agenda.R
import me.diegoramos.agenda.dao.ContactDAO
import me.diegoramos.agenda.model.Contact
import me.diegoramos.agenda.ui.adapter.ContactItemAdapter
import me.diegoramos.agenda.ui.adapter.listener.OnItemClickListener
import me.diegoramos.agenda.ui.adapter.listener.OnItemLongClickListener

class MainActivity : AppCompatActivity(), OnItemClickListener, OnItemLongClickListener {

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

    override fun onItemClick(contact: Contact, position: Int) {
        val intent = Intent(applicationContext, ContactFormActivity::class.java)
        intent.putExtra(Constants.CONTACT_EXTRA_NAME, contact)
        intent.putExtra(Constants.CONTACT_POSITION_EXTRA_NAME, position)
        startActivityForResult(intent, Constants.UPDATE_CONTACT_REQUEST_CODE)
    }

    override fun onItemLongClick(contact: Contact, position: Int) {
        dialogToRemove(contact, position)
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
                ContactDAO.remove(contact)
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

    private fun isCanceledResult(
        resultCode: Int
    ) = resultCode == Activity.RESULT_CANCELED

    private fun isOkResult(resultCode: Int) =
        resultCode == Activity.RESULT_OK

    private fun isCreateContactRequest(requestCode: Int) =
        requestCode == Constants.CREATE_CONTACT_REQUEST_CODE

    private fun configureRecyclerView() {
        this.activity_main_contact_list.adapter = ContactItemAdapter(ContactDAO.getAll(),
            this,
            this)
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
        val receivedContact: Contact = data.getSerializableExtra(Constants.CONTACT_EXTRA_NAME) as Contact
        ((this.activity_main_contact_list.adapter) as ContactItemAdapter).addContact(receivedContact)
        this.activity_main_contact_list.adapter?.notifyDataSetChanged()

        handleSavedContactFeedback(receivedContact)
    }

    private fun handleUpdatedItemOnAdapter(data: Intent) {
        val receivedContact: Contact = data.getSerializableExtra(Constants.CONTACT_EXTRA_NAME) as Contact
        val receivedPosition: Int = data.getIntExtra(Constants.CONTACT_POSITION_EXTRA_NAME, -1)
        ((this.activity_main_contact_list.adapter) as ContactItemAdapter).updateContact(receivedContact, receivedPosition)
        this.activity_main_contact_list.adapter?.notifyDataSetChanged()

        handleSavedContactFeedback(receivedContact)
    }

    private fun handleRemovedItemOnAdapter(position: Int) {
        ((this.activity_main_contact_list.adapter) as ContactItemAdapter).removeContact(position)
    }

    private fun handleSavedContactFeedback(contact: Contact) =
        Toast.makeText(this, "Contact ${contact.name} saved!", Toast.LENGTH_LONG).show()

}