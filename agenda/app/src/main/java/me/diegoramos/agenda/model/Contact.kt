package me.diegoramos.agenda.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
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

data class ContactAndPhones (
   @Embedded
   val contact: Contact,
   @Relation(
      parentColumn = "id",
      entityColumn = "contactId"
   )
   val phones: List<Phone> = listOf()
) : Serializable