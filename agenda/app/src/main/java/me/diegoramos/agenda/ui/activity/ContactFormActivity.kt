package me.diegoramos.agenda.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_student_form.*
import me.diegoramos.agenda.Constants
import me.diegoramos.agenda.R
import me.diegoramos.agenda.dao.ContactDAO
import me.diegoramos.agenda.model.Contact
import me.diegoramos.agenda.model.DuplicatedItemException

class ContactFormActivity : AppCompatActivity() {

    private var mode: FormMode = FormMode.REGISTER
    private var currentContact: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_form)
        setTitle(R.string.student_form_activity_title)
        handleEdit()
    }

    override fun onResume() {
        super.onResume()
        if(!haveStudentExtra()) {
            resetFields()
        }
    }

    private fun haveStudentExtra(): Boolean {
        val data: Intent = intent
        val student =
            data.getSerializableExtra(Constants.contactExtraName) as Contact?

        return student != null
    }

    private fun handleEdit() {
        val data: Intent = intent
        val student =
            data.getSerializableExtra(Constants.contactExtraName) as Contact?

        if(student != null) {
            setTitle(R.string.student_form_activity_edit_title)
            activity_student_form_name?.setText(student.name)
            activity_student_form_email?.setText(student.email)
            activity_student_form_phone?.setText(student.phone)
            mode = FormMode.UPDATE
            currentContact = student
        }
    }

    private fun resetFields() {
        activity_student_form_name?.text = null
        activity_student_form_email?.text = null
        activity_student_form_phone?.text = null
    }

    fun handleSave(view: View) {
        val contact: Contact?

        try {
            if (mode == FormMode.REGISTER) {
                contact = Contact(name = activity_student_form_name?.text.toString(),
                    email = activity_student_form_email?.text.toString(),
                    phone = activity_student_form_phone?.text.toString())
                handleRegister(contact)
            } else {
                contact = Contact(id = currentContact!!.id, name = activity_student_form_name?.text.toString(),
                    email = activity_student_form_email?.text.toString(),
                    phone = activity_student_form_phone?.text.toString())
                handleUpdate(contact)
            }

            Toast.makeText(this, "Contact ${activity_student_form_name?.text.toString()} saved!", Toast.LENGTH_LONG).show()

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
        intent.putExtra(Constants.contactExtraName, contact)

        return intent
    }

}

enum class FormMode {
    REGISTER, UPDATE
}