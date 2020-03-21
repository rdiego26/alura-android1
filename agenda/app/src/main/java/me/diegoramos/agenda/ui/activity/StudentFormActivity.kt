package me.diegoramos.agenda.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import me.diegoramos.agenda.R
import me.diegoramos.agenda.dao.StudentDAO
import me.diegoramos.agenda.model.Student

class StudentFormActivity : AppCompatActivity() {

    private var nameField: EditText? = null
    private var emailField: EditText? = null
    private var phoneField: EditText? = null
    private val studentDao = StudentDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_form)
        setTitle(R.string.student_form_activity_title)
        initializeComponents()
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
            data.getSerializableExtra(R.string.constant_student_extra.toString()) as Student?

        return student != null
    }

    private fun handleEdit() {
        val data: Intent = intent
        val student =
            data.getSerializableExtra(R.string.constant_student_extra.toString()) as Student?

        if(student != null) {
            nameField?.setText(student.name)
            emailField?.setText(student.email)
            phoneField?.setText(student.phone)
        }
    }

    private fun initializeComponents() {
        nameField = findViewById(R.id.activity_student_form_name)
        emailField = findViewById(R.id.activity_student_form_email)
        phoneField = findViewById(R.id.activity_student_form_phone)
    }

    private fun resetFields() {
        nameField = findViewById(R.id.activity_student_form_name)
        emailField = findViewById(R.id.activity_student_form_email)
        phoneField = findViewById(R.id.activity_student_form_phone)

        nameField?.text = null
        emailField?.text = null
        phoneField?.text = null
    }

    fun handleSave(view: View) {
        val studentName = nameField?.text.toString()
        val student = Student(name = studentName, email = emailField?.text.toString(),
            phone= phoneField?.text.toString())

        studentDao.add(student)
        Toast.makeText(this, "Student $studentName added!", Toast.LENGTH_LONG).show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
