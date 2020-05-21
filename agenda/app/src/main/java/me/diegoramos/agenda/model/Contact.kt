package me.diegoramos.agenda.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
class Contact (
   @PrimaryKey
   val id: UUID = UUID.randomUUID(),
   val name: String,
   val email: String,
   val phone: String
) : Serializable