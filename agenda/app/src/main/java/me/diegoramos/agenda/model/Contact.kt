package me.diegoramos.agenda.model

import java.io.Serializable
import java.util.*

class Contact (val id: UUID = UUID.randomUUID(),
               val name: String,
               val email: String,
               val phone: String) : Serializable