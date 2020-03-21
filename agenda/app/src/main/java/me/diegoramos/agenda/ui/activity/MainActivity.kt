package me.diegoramos.agenda.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import me.diegoramos.agenda.R
import me.diegoramos.agenda.dao.StudentDAO

class MainActivity : AppCompatActivity() {

    private val studentDAO = StudentDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.main_activity_title)

        val listView: ListView = findViewById(R.id.activity_student_list)
        val studentList = studentDAO.getAll().map { it.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, studentList)

        listView.adapter = adapter
    }

    fun goToForm(view: View) {
        val intent = Intent(this, StudentFormActivity::class.java)
        startActivity(intent)
    }
}