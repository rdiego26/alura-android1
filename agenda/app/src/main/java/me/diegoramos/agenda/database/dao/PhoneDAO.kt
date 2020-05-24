package me.diegoramos.agenda.database.dao

import androidx.room.*
import me.diegoramos.agenda.model.Phone
import java.util.*

@Dao
interface PhoneDAO {

    @Insert
    fun save(phone: Phone)

    @Query("SELECT * FROM Phone WHERE contactId = :contactId")
    fun getAllByContact(contactId: UUID): MutableList<Phone>

    @Delete
    fun remove(phone: Phone)
    
    @Update
    fun update(phone: Phone)

}