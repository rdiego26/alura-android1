package me.diegoramos.agenda.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import me.diegoramos.agenda.R
import me.diegoramos.agenda.dao.StudentDAO
import me.diegoramos.agenda.model.Student

class MainActivity : AppCompatActivity() {

    private val studentDAO = StudentDAO
    private var studentListView: ListView? = null
    private var studentList = listOf<Student>()
    private var listAdapter: ArrayAdapter<Student>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setTitle(R.string.main_activity_title)

        initializeComponents()
        setupList()
    }

    override fun onResume() {
        super.onResume()
        setupList()
    }

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(R.string.student_list_context_menu_remove)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val menuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val selectedStudent = listAdapter?.getItem(menuInfo.position) as Student
        studentDAO.remove(selectedStudent)

        Toast.makeText(
            this,
            String.format(
                resources.getString(R.string.removed_student_message),
                selectedStudent.name
            ),
            Toast.LENGTH_SHORT
        ).show()
        this.onResume()

        return super.onContextItemSelected(item)
    }

    private fun initializeComponents() {
        studentListView = findViewById(R.id.activity_student_list)
    }

    private fun setupList() {
        studentList = cleanAndOrderData(studentDAO.getAll())
        listAdapter = createAdapter(studentList)
        studentListView?.adapter = listAdapter

        configureListItemClick()
        registerForContextMenu(studentListView)
    }

    private fun configureListItemClick() {
        studentListView?.setOnItemClickListener { parent, view, position, id ->
            val selectedStudent = parent.getItemAtPosition(position) as Student
            val intent = prepareIntentToForm()
            intent.putExtra(R.string.constant_student_extra.toString(), selectedStudent)
            startActivity(intent)
        }
    }

    fun goToForm(view: View) {
        val intent = prepareIntentToForm()
        startActivity(intent)
    }

    private fun createAdapter(studentList: List<Student>) =
        ArrayAdapter(this, android.R.layout.simple_list_item_1, studentList)

    private fun cleanAndOrderData(list: List<Student>) =
        list.sortedBy { it.name }

    private fun prepareIntentToForm() =
        Intent(this, StudentFormActivity::class.java)
}