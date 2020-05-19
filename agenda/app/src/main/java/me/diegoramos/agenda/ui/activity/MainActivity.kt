package me.diegoramos.agenda.ui.activity

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

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.main_activity_title)

        ContactDAO.add(Contact(name = "Nome", email = "email@email.com", phone = "9999999"), this)

        configureRecyclerView()
        configureFABToForm()
//        setupList()
    }

//    override fun onCreateContextMenu(
//        menu: ContextMenu?,
//        v: View?,
//        menuInfo: ContextMenu.ContextMenuInfo?
//    ) {
//        super.onCreateContextMenu(menu, v, menuInfo)
//        menuInflater.inflate(R.menu.activity_main_menu, menu)
//    }
//
//    override fun onContextItemSelected(item: MenuItem): Boolean {
//        val menuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo
//        val selectedStudent = listAdapter?.getItem(menuInfo.position) as Contact
//
//        when (item.itemId) {
//            R.id.activity_main_context_menu_delete -> {
//                dialogToRemove(selectedStudent)
//                this.onResume()
//            }
//        }
//
//        return super.onContextItemSelected(item)
//    }

    private fun dialogToRemove(contact: Contact) {
        val message = String.format(resources.getString(R.string.remove_student_question),
            contact.name)
        val btnConfirm = resources.getString(R.string.remove_student_confirmed)
        val btnCancel = resources.getString(R.string.remove_student_cancel)

        AlertDialog.Builder(this)
            .setTitle(R.string.remove_student_title)
            .setMessage(message)
            .setPositiveButton(btnConfirm) { _, _ ->
                ContactDAO.remove(contact)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (isCreateContactRequest(requestCode) && isCreateContactResult(resultCode) && hasContact(data)) {
            handleAddedItemOnAdapter(data!!)
        } else if(isUpdateContactRequest(requestCode) && isUpdateContactResult(resultCode) && hasContact(data)) {
            handleUpdatedItemOnAdapter(data!!)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun configureRecyclerView() {
        this.activity_main_contact_list.adapter = ContactItemAdapter(ContactDAO.getAll()){
            val intent = Intent(applicationContext, ContactFormActivity::class.java)
            intent.putExtra(Constants.CONTACT_EXTRA_NAME, it)
            startActivityForResult(intent, Constants.updatedContactRequestCode)
        }
    }

//    private fun setupList() {
//        studentList = cleanAndOrderData(studentDAO.getAll())
//        listAdapter = createAdapter(studentList)
//        studentListView?.adapter = listAdapter
//
//        configureListItemClick()
//        registerForContextMenu(studentListView)
//    }

//    private fun configureListItemClick() {
//        studentListView?.setOnItemClickListener { parent, view, position, id ->
//            val selectedStudent = parent.getItemAtPosition(position) as Contact
//            val intent = prepareIntentToForm()
//            intent.putExtra(R.string.constant_student_extra.toString(), selectedStudent)
//            startActivity(intent)
//        }
//    }

    private fun configureFABToForm() {
        activity_main_fab.setOnClickListener {
            val intent = Intent(applicationContext, ContactFormActivity::class.java)
            startActivityForResult(intent, Constants.createdContactRequestCode)
        }
    }

    private fun hasContact(data: Intent?) =
        data?.hasExtra(Constants.CONTACT_EXTRA_NAME)!!

    private fun isUpdateContactResult(resultCode: Int) =
        resultCode == Constants.updatedContactResultCode

    private fun isUpdateContactRequest(requestCode: Int) =
        requestCode == Constants.updatedContactRequestCode

    private fun isCreateContactResult(resultCode: Int) =
        resultCode == Constants.createdContactResultCode

    private fun isCreateContactRequest(requestCode: Int) =
        requestCode == Constants.createdContactRequestCode

    private fun handleAddedItemOnAdapter(data: Intent) {
        val receivedContact: Contact = data.getSerializableExtra(Constants.CONTACT_EXTRA_NAME) as Contact
        ((this.activity_main_contact_list.adapter) as ContactItemAdapter).addContact(receivedContact)
        this.activity_main_contact_list.adapter?.notifyDataSetChanged()
    }

    private fun handleUpdatedItemOnAdapter(data: Intent) {
        val receivedContact: Contact = data.getSerializableExtra(Constants.CONTACT_EXTRA_NAME) as Contact
        ((this.activity_main_contact_list.adapter) as ContactItemAdapter).updateContact(receivedContact)
        this.activity_main_contact_list.adapter?.notifyDataSetChanged()
    }

}