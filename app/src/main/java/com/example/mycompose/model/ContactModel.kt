package com.example.mycompose.model


data class ContactModel(
    val index: Int,
    val id: Long,
    val name: String,
    val avatar: String,
    val phone: ArrayList<PhoneModel>,
    val email: ArrayList<EmailModel>,
)
