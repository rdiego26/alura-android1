package me.diegoramos.agenda.dao

import android.content.Context
import me.diegoramos.agenda.R
import me.diegoramos.agenda.model.DuplicatedItemException
import me.diegoramos.agenda.model.Student

object StudentDAO {

    var students: List<Student> = listOf()

    fun add(student: Student, context: Context) {
        validateAddOrUpdate(student, context)
        students = students + student
    }

    fun getByName(name: String): Student? =
        students.find { it.name == name }

    fun update(student: Student, context: Context) {
        validateAddOrUpdate(student, context)
        students = students.filterNot { it.id == student.id } + student
    }

    fun getAll(): List<Student> {
        return students
    }

    private fun validateAddOrUpdate(student: Student, context: Context) {
        val resources = context.resources
        val alreadyWithSameName = students.any { it.name == student.name && it.id != student.id }
        if(alreadyWithSameName) {
            throw DuplicatedItemException(String.format( resources.getString(R.string.duplicated_item_by_name_message), student.name))
        }

        val alreadyWithSameEmail = students.any { it.email == student.email && it.id != student.id }
        if(alreadyWithSameEmail) {
            throw DuplicatedItemException(String.format( resources.getString(R.string.duplicated_item_by_email_message), student.email))
        }

        val alreadyWithSamePhone = students.any { it.phone == student.phone && it.id != student.id }
        if(alreadyWithSamePhone) {
            throw DuplicatedItemException(String.format( resources.getString(R.string.duplicated_item_by_phone_message), student.phone))
        }
    }
}

