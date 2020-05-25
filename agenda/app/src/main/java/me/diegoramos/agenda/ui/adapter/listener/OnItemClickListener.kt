package me.diegoramos.agenda.ui.adapter.listener

import me.diegoramos.agenda.model.ContactAndPhones

interface OnItemClickListener {

    fun onItemClick(item: ContactAndPhones, position: Int)
}