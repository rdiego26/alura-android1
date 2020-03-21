package me.diegoramos.agenda.model

import java.io.Serializable
import java.util.UUID

class Student (val id: UUID = UUID.randomUUID(),
               val name: String,
               val email: String,
               val phone: String) : Serializable