package me.diegoramos.agenda.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_contact.view.*
import me.diegoramos.agenda.R
import me.diegoramos.agenda.model.Contact
import me.diegoramos.agenda.ui.adapter.listener.OnItemClickListener
import java.util.*

class ContactItemAdapter(private val data: MutableList<Contact>,
                         private val itemClickListener: OnItemClickListener
): RecyclerView.Adapter<ContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ContactViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) =
        holder.bind(data[position], itemClickListener)

    fun addContact(contact: Contact) {
        data.add(contact)
        notifyDataSetChanged()
    }

    fun updateContact(contact: Contact, position: Int) {
        data[position] = contact
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

class ContactViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_contact, parent, false)) {

    fun bind(contact: Contact, itemClickListener: OnItemClickListener) {
        itemView.item_contact_name.text = contact.name
        itemView.item_contact_phone.text = contact.phone
        itemView.item_contact_email.text = contact.email

        itemView.setOnClickListener { itemClickListener.onItemClick (contact, adapterPosition) }
    }
}