package me.diegoramos.agenda.dao

import me.diegoramos.agenda.model.Student

object StudentDAO {

    var students: List<Student> = listOf()

    fun add(student: Student) {
        students = students + student
    }

    fun getAll(): List<Student> {
        return students
    }
}

