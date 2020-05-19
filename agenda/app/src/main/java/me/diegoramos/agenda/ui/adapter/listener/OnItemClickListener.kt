package me.diegoramos.agenda.ui.adapter.listener

import me.diegoramos.agenda.model.Contact

interface OnItemClickListener {

    fun onItemClick(contact: Contact, position: Int)
}