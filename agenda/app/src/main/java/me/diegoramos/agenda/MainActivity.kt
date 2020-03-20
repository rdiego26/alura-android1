package me.diegoramos.agenda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView: ListView = findViewById(R.id.activity_student_list)
        val studentList = listOf("Aluno 1", "Aluno 2")
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, studentList)

        listView.adapter = adapter
    }
}
