package me.diegoramos.agenda.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_contact.view.*
import me.diegoramos.agenda.R
import me.diegoramos.agenda.model.Contact

class ContactItemAdapter(private val data: MutableList<Contact>,
                         private val click: (item: Contact) -> Unit): RecyclerView.Adapter<ContactViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ContactViewHolder(inflater, parent)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) =
        holder.bind(data[position], click)

    fun addContact(contact: Contact) {
        data.add(contact)
        notifyDataSetChanged()
    }
}

class ContactViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.item_contact, parent, false)) {

    fun bind(contact: Contact, click: (item: Contact) -> Unit) {
        itemView.item_contact_name.text = contact.name
        itemView.item_contact_phone.text = contact.phone

        itemView.setOnClickListener { click(contact) }
    }
}