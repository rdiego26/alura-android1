package me.diegoramos.agenda.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import me.diegoramos.agenda.R
import me.diegoramos.agenda.model.ContactAndPhones
import me.diegoramos.agenda.model.PhoneType
import java.util.*

class ContactItemAdapter(private val data: MutableList<ContactAndPhones>,
                         private val events: Events
): RecyclerView.Adapter<RecyclerView.ViewHolder> () {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolderV2 {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_contact, parent, false)
        return ContactViewHolderV2(itemView)
    }

    interface Events {
        fun onItemClickListener(item: ContactAndPhones, position: Int)
        fun onItemLongClickListener(item: ContactAndPhones, position: Int): Boolean
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val item = holder as ContactViewHolderV2

        item.run {
            this.name.text = data[position].contact.fullName()
            this.email.text = data[position].contact.email
            this.phone.text = data[position].phones.firstOrNull { it.type == PhoneType.HOME }?.number
            this.mobile.text = data[position].phones.firstOrNull { it.type == PhoneType.MOBILE }?.number

            this.cardView.run {
                this.setOnClickListener { events.onItemClickListener(data[position], position) }
                this.setOnLongClickListener { events.onItemLongClickListener(data[position], position) }
            }
        }

    }

    fun addContact(item: ContactAndPhones) {
        data.add(item)
        notifyDataSetChanged()
    }

    fun updateContact(item: ContactAndPhones, position: Int) {
        data[position] = item
        notifyItemChanged(position)
    }

    fun removeContact(position: Int) {
        data.removeAt(position)
        notifyItemRemoved(position)
    }

    fun changePosition(initialPosition: Int, newPosition: Int) {
        Collections.swap(data, initialPosition, newPosition)
        notifyItemMoved(initialPosition, newPosition)
    }

}

class ContactViewHolderV2(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val name: TextView = itemView.findViewById(R.id.item_contact_name)
    val email: TextView = itemView.findViewById(R.id.item_contact_email)
    val phone: TextView = itemView.findViewById(R.id.item_contact_phone)
    val mobile: TextView = itemView.findViewById(R.id.item_contact_mobile)
    val cardView: CardView = itemView.findViewById(R.id.cardView)
}
