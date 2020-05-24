package me.diegoramos.agenda.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*


@Entity
class Phone(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val number: String,
    @ColumnInfo(defaultValue = "HOME")
    val type: PhoneType = PhoneType.HOME,
    @ForeignKey(
        entity = Contact::class,
        parentColumns = ["id"],
        childColumns = ["contactId"],
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )
    val contactId: UUID
)

enum class PhoneType {
    HOME, MOBILE, WORK, OTHERS
}