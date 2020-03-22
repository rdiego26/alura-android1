package me.diegoramos.agenda.model

import java.io.Serializable
import java.util.*

class Student (val id: UUID = UUID.randomUUID(),
               val name: String,
               val email: String,
               val phone: String) : Serializable {
    override fun toString(): String {
        return name
    }
}