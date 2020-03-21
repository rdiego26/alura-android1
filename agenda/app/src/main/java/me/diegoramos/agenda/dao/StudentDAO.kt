package me.diegoramos.agenda.dao

import me.diegoramos.agenda.model.DuplicatedItemException
import me.diegoramos.agenda.model.Student

object StudentDAO {

    var students: List<Student> = listOf()

    fun add(student: Student) {
        validateAddOrUpdate(student)
        students = students + student
    }

    fun getByName(name: String): Student? =
        students.find { it.name == name }

    fun update(student: Student) {
        validateAddOrUpdate(student)
        students = students.filterNot { it.id == student.id } + student
    }

    fun getAll(): List<Student> {
        return students
    }

    private fun validateAddOrUpdate(student: Student) {
        val alreadyWithSameName = students.any { it.name == student.name && it.id != student.id }
        if(alreadyWithSameName) {
            throw DuplicatedItemException("Already other student with name ${student.name}")
        }

        val alreadyWithSameEmail = students.any { it.email == student.email && it.id != student.id }
        if(alreadyWithSameEmail) {
            throw DuplicatedItemException("Already other student with email ${student.email}")
        }

        val alreadyWithSamePhone = students.any { it.phone == student.phone && it.id != student.id }
        if(alreadyWithSamePhone) {
            throw DuplicatedItemException("Already other student with phone ${student.phone}")
        }
    }
}

