package com.example.mycompose.helper

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.BaseColumns
import android.provider.ContactsContract
import com.example.mycompose.model.ContactModel
import com.example.mycompose.model.EmailModel
import com.example.mycompose.model.PhoneModel


object ContactHelper {

    @SuppressLint("Range")
    fun getContactsFromPhoneBook(context: Context, strNameFilter: String? = null): ArrayList<ContactModel>? {
        val arrayListContacts: ArrayList<ContactModel> = ArrayList()
        var index = 0

        // filter by characters of name
        var whereName: String? = null
        var whereNameParams = arrayOf<String>()

        strNameFilter?.let { name ->
            whereName = ContactsContract.Contacts.DISPLAY_NAME + " like ?"
            whereNameParams = arrayOf("%$name%")
        }

        val cr: ContentResolver = context.getContentResolver()
        val cur = cr.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            whereName,
            whereNameParams,
            ContactsContract.Contacts.SORT_KEY_PRIMARY + " ASC"
        )
        if (cur != null) {
            while (cur.moveToNext()) {
                val columnIndex = cur.getString(cur.getColumnIndexOrThrow(BaseColumns._ID))
                val idContact = cur.getLong(cur.getColumnIndexOrThrow(BaseColumns._ID))
                val nameContact = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME))
                val avatarContact = cur.getString(cur.getColumnIndexOrThrow(ContactsContract.PhoneLookup.PHOTO_THUMBNAIL_URI))

                // get the contact's phone numbers
                var phoneList: ArrayList<PhoneModel> = ArrayList()
                if (cur.getString(cur.getColumnIndexOrThrow(ContactsContract.Contacts.HAS_PHONE_NUMBER)).toInt() > 0) {
                    val phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", arrayOf(columnIndex), null)
                    if (phones != null) {
                        while (phones.moveToNext()) {
                            val numberContact = phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            val numberTypeNum = phones.getInt(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.TYPE))

                            val numberTypeStr = when (numberTypeNum) {
                                ContactsContract.CommonDataKinds.Phone.TYPE_HOME -> "Home"
                                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> "Mobile"
                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK -> "Work"
                                ContactsContract.CommonDataKinds.Phone.TYPE_CAR -> "Car"
                                ContactsContract.CommonDataKinds.Phone.TYPE_RADIO -> "Radio"
                                ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME -> "Fax home"
                                ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK -> "Fax work"
                                ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE -> "Mobile work"
                                ContactsContract.CommonDataKinds.Phone.TYPE_MAIN -> "Main"
                                else -> "Other"
                            }
                            phoneList.add(PhoneModel(numberContact, numberTypeStr, numberTypeNum))
                        }
                    }
                    phones?.close()
                }

                // get the contact's email address
                var emailList: ArrayList<EmailModel> = ArrayList()
                val cursorMail: Cursor? = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", arrayOf(columnIndex), null)

                if (cursorMail != null) {
                    while (cursorMail.moveToNext()) {

                        val emailContact = cursorMail.getString(cursorMail.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                        val emailTypeNum = cursorMail.getInt(cursorMail.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE))

                        var emailTypeStr = when (cursorMail.getInt(cursorMail.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE))) {
                            ContactsContract.CommonDataKinds.Email.TYPE_HOME -> "Home"
                            ContactsContract.CommonDataKinds.Email.TYPE_MOBILE -> "Mobile"
                            ContactsContract.CommonDataKinds.Email.TYPE_WORK -> "Work"
                            ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM -> "Custom"
                            ContactsContract.CommonDataKinds.Email.TYPE_OTHER -> "Other"
                            else -> "Other"
                        }
                        emailList.add(EmailModel(emailContact, emailTypeStr, emailTypeNum))
                    }
                    cursorMail.close()
                }

                arrayListContacts.add(
                    ContactModel(
                        index++,
                        idContact,
                        nameContact,
                        avatarContact ?: "",
                        phoneList,
                        emailList
                    )
                )

            }
            // remove duplicate numbers
            val hashSet: LinkedHashSet<ContactModel> = LinkedHashSet()
            hashSet.addAll(arrayListContacts)
            arrayListContacts.clear()
            arrayListContacts.addAll(hashSet)

            cur.close()
        }
        return arrayListContacts
    }

    // update phone number with raw contact id and phone type.
    fun updatePhoneNumber(
        context: Context,
        contactId: Long,
        phoneType: Int,
        newPhoneNumber: String
    ) {
        val contentResolver: ContentResolver = context.getContentResolver()
        // create content values object.
        val contentValues = ContentValues()

        // put new phone number value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNumber)

        // create query condition, query with the raw contact id.
        val whereClauseBuf = StringBuffer()

        // specify the update contact id.
        whereClauseBuf.append(ContactsContract.Data.RAW_CONTACT_ID)
        whereClauseBuf.append("=")
        whereClauseBuf.append(contactId)

        // specify the row data mimetype to phone mimetype( vnd.android.cursor.item/phone_v2 )
        whereClauseBuf.append(" and ")
        whereClauseBuf.append(ContactsContract.Data.MIMETYPE)
        whereClauseBuf.append(" = '")
        val mimetype = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
        whereClauseBuf.append(mimetype)
        whereClauseBuf.append("'")

        // specify phone type.
        whereClauseBuf.append(" and ")
        whereClauseBuf.append(ContactsContract.CommonDataKinds.Phone.TYPE)
        whereClauseBuf.append(" = ")
        whereClauseBuf.append(phoneType)

        // update phone info through Data uri.Otherwise it may throw java.lang.UnsupportedOperationException.
        val dataUri: Uri = ContactsContract.Data.CONTENT_URI

        // get update data count.
        val updateCount =
            contentResolver.update(dataUri, contentValues, whereClauseBuf.toString(), null)
    }
}