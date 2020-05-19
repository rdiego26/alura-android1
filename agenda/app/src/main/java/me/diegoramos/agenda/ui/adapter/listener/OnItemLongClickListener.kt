package me.diegoramos.agenda.ui.adapter.listener

import me.diegoramos.agenda.model.Contact

interface OnItemLongClickListener {

    fun onItemLongClick(contact: Contact, position: Int)
}