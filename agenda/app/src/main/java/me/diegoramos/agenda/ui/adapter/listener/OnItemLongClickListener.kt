package me.diegoramos.agenda.ui.adapter.listener

import me.diegoramos.agenda.model.ContactAndPhones

interface OnItemLongClickListener {

    fun onItemLongClick(item: ContactAndPhones, position: Int)
}