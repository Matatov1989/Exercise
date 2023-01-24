package com.example.mycompose

import android.content.Context
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import com.example.mycompose.helper.ContactHelper
import com.example.mycompose.model.ContactModel
import com.example.mycompose.model.PhoneModel

class ContactViewModel : ViewModel() {

    private val contactsLiveData = listOf<ContactModel>().toMutableStateList()
    val listContact: List<ContactModel>
        get() = contactsLiveData


    // get all contacts from phone book
    fun fetchContacts(context: Context, strNameFilter: String? = null) {
        contactsLiveData.clear()
        ContactHelper.getContactsFromPhoneBook(context = context, strNameFilter = strNameFilter)?.forEach { contact ->
            contactsLiveData.add(contact)
        }
    }

    // update phone numbers to contact
    fun updatePhone(context: Context, contact: ContactModel, phone: PhoneModel, newPhoneNumber: String) {
        contactsLiveData.set(contact.index, contact)
        ContactHelper.updatePhoneNumber(
            context = context,
            contactId = contact.id,
            phoneType = phone.typeNum,
            newPhoneNumber = newPhoneNumber
        )
    }
}