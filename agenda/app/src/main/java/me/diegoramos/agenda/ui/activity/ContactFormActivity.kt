package me.diegoramos.agenda.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_student_form.*
import me.diegoramos.agenda.Constants
import me.diegoramos.agenda.R
import me.diegoramos.agenda.dao.StudentDAO
import me.diegoramos.agenda.model.Contact
import me.diegoramos.agenda.model.DuplicatedItemException

class ContactFormActivity : AppCompatActivity() {

    private var mode: FormMode = FormMode.REGISTER
    private var currentContact: Contact? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_form)
        setTitle(R.string.student_form_activity_title)
//        initializeComponents()
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
            data.getSerializableExtra(R.string.constant_student_extra.toString()) as Contact?

        return student != null
    }

    private fun handleEdit() {
        val data: Intent = intent
        val student =
            data.getSerializableExtra(R.string.constant_student_extra.toString()) as Contact?

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
        val studentName = activity_student_form_name?.text.toString()
        val contact: Contact?

        try {
            if (mode == FormMode.REGISTER) {
                contact = Contact(name = studentName, email = activity_student_form_email?.text.toString(),
                    phone = activity_student_form_phone?.text.toString())
                StudentDAO.add(contact, this)
                setResult(Constants.createdContactResultCode, prepareResultForRegister(contact))
            } else {
                contact = Contact(id = currentContact!!.id, name = studentName, email = activity_student_form_email?.text.toString(),
                    phone = activity_student_form_phone?.text.toString())
                StudentDAO.update(contact, this)
                setResult(Constants.updatedContactResultCode, prepareResultForUpdate(contact))
            }

            Toast.makeText(this, "Student $studentName saved!", Toast.LENGTH_LONG).show()


            finish()
        } catch (ex: DuplicatedItemException) {
            Toast.makeText(this, ex.message, Toast.LENGTH_LONG).show()
        }

    }

    private fun prepareResultForRegister(contact: Contact): Intent {
        val intent = Intent()
        intent.putExtra(Constants.createdContactExtraName, contact)

        return intent
    }

    private fun prepareResultForUpdate(contact: Contact): Intent {
        val intent = Intent()
        intent.putExtra(Constants.updatedContactExtraName, contact)

        return intent
    }
}

enum class FormMode {
    REGISTER, UPDATE
}