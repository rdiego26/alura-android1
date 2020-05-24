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
   val lastName: String,
   val email: String
) : Serializable {

   fun fullName() = "$name $lastName"

}