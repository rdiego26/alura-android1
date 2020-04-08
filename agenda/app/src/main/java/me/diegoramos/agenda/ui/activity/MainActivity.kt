package me.diegoramos.agenda.ui.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import me.diegoramos.agenda.R
import me.diegoramos.agenda.dao.StudentDAO
import me.diegoramos.agenda.model.Student

class MainActivity : AppCompatActivity() {

    private val studentDAO = StudentDAO
    private var studentListView: ListView? = null
    private var studentList = listOf<Student>()
    private var listAdapter: ContactItemAdapter? = null

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
        menuInflater.inflate(R.menu.activity_main_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val menuInfo = item.menuInfo as AdapterView.AdapterContextMenuInfo
        val selectedStudent = listAdapter?.getItem(menuInfo.position) as Student

        when (item.itemId) {
            R.id.activity_main_context_menu_delete -> {
                dialogToRemove(selectedStudent)
                this.onResume()
            }
        }

        return super.onContextItemSelected(item)
    }

    private fun dialogToRemove(student: Student) {
        val message = String.format(resources.getString(R.string.remove_student_question),
            student.name)
        val btnConfirm = resources.getString(R.string.remove_student_confirmed)
        val btnCancel = resources.getString(R.string.remove_student_cancel)

        AlertDialog.Builder(this)
            .setTitle(R.string.remove_student_title)
            .setMessage(message)
            .setPositiveButton(btnConfirm) { _, _ ->
                studentDAO.remove(student)
                Toast.makeText(this, String.format(
                    resources.getString(R.string.removed_student_message),
                    student.name),
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
        ContactItemAdapter(this, studentList)


    private fun cleanAndOrderData(list: List<Student>) =
        list.sortedBy { it.name }

    private fun prepareIntentToForm() =
        Intent(this, StudentFormActivity::class.java)

    private class ContactItemAdapter(context: Context, list: List<Student>) : BaseAdapter() {
        internal var sList = list
        private val mInflator: LayoutInflater = LayoutInflater.from(context)

        override fun getCount(): Int {
            return sList.size
        }

        override fun getItem(position: Int): Any {
            return sList[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            val view: View?
            val vh: ListRowHolder
            if (convertView == null) {
                view = this.mInflator.inflate(R.layout.item_contact, parent, false)
                vh = ListRowHolder(view)
                view.tag = vh
            } else {
                view = convertView
                vh = view.tag as ListRowHolder
            }

            vh.name.text = sList[position].name
            vh.phone.text = sList[position].phone
            return view
        }
    }

    private class ListRowHolder(row: View?) {
        val name: TextView = row?.findViewById(R.id.item_contact_name) as TextView
        val phone: TextView = row?.findViewById(R.id.item_contact_phone) as TextView
    }
}