package me.diegoramos.agenda.model

class DuplicatedItemException(errorMessage: String) : Exception(errorMessage)
class BlankRequiredFieldException(errorMessage: String) : Exception(errorMessage)