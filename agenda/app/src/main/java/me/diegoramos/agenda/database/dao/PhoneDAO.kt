package me.diegoramos.agenda.database.dao

import androidx.room.*
import me.diegoramos.agenda.model.Phone

@Dao
interface PhoneDAO {

    @Insert
    fun save(phone: Phone)

    @Query("SELECT * FROM Phone WHERE contactId = :contactId")
    fun getAllByContact(contactId: String): MutableList<Phone>

    @Delete
    fun remove(phone: Phone)
    
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(phone: Phone)

}