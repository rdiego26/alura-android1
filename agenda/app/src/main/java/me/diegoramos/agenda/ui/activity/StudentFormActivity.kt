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

        nameField = findViewById(R.id.activity_student_form_name)
        emailField = findViewById(R.id.activity_student_form_email)
        phoneField = findViewById(R.id.activity_student_form_phone)
    }

    fun handleSave(view: View) {
        val studentName = nameField?.text.toString()
        val student = Student(studentName, emailField?.text.toString(),
            phoneField?.text.toString())

        studentDao.add(student)
        Toast.makeText(this, "Student $studentName added!", Toast.LENGTH_LONG).show()

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}
