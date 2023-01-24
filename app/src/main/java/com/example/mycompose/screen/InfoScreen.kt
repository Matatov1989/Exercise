package com.example.mycompose.screen

import android.Manifest
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.example.mycompose.ContactViewModel
import com.example.mycompose.MainActivity
import com.example.mycompose.model.ContactModel
import com.example.mycompose.model.EmailModel
import com.example.mycompose.model.PhoneModel
import com.google.accompanist.permissions.*

private var mainActivity: MainActivity? = null
private var viewModel: ContactViewModel? = null
private var isApproved = false

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoScreen(
    contact: ContactModel? = null,
    navController: NavController,
    _mainActivity: MainActivity
) {

    isApproved = PermissionWriteContacts()

    mainActivity = _mainActivity
    viewModel = _mainActivity.viewModel

    val scrollState = rememberScrollState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = scrollState)
            .padding(16.dp)
    ) {

        // avatar ot first char of contact's name
        if (contact?.avatar.equals("")) {
            Text(
                text = contact?.name!!.substring(0, 1),
                fontSize = 70.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(
                        Color.Green,
                    )
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(contact!!.avatar),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = Color.Green, shape = CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        // contact's name
        Text(
            text = contact!!.name,
            fontWeight = FontWeight.Bold,
            fontSize = 35.sp
        )

        Spacer(modifier = Modifier.width(16.dp).padding(top = 10.dp))

        // phone numbers
        Text(
            text = "Phones:",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        contact.phone.forEach { phone ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {
                var textPhone by remember { mutableStateOf(TextFieldValue(phone.phone)) }
                OutlinedTextField(
                    modifier = Modifier.fillMaxSize(),
                    value = textPhone,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    label = { Text(text = "${phone.typeStr}") },
                    onValueChange = { textForFilter ->

                        if (isApproved) {
                            textPhone = textForFilter

                            phone.phone = textForFilter.text

                            viewModel?.updatePhone(
                                context = mainActivity?.baseContext!!,
                                contact = contact!!,
                                phone = phone,
                                newPhoneNumber = textForFilter.text
                            )
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp).padding(top = 20.dp))

        // e-mails
        Text(
            text = "E-mails:",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        contact.email.forEach { email ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 8.dp)
            ) {

                var textEmail by remember { mutableStateOf(TextFieldValue(email.email)) }
                OutlinedTextField(
                    modifier = Modifier.fillMaxSize(),
                    value = textEmail,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    label = { Text(text = "${email.typeStr}") },
                    onValueChange = { textForFilter ->
                        textEmail = textForFilter
                    }
                )
            }
        }

        // back to a ListScreen
        BackHandler {
            viewModel?.fetchContacts(mainActivity?.baseContext!!)
            navController.popBackStack()
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionWriteContacts(): Boolean {

    val contactPermissionState = rememberPermissionState(
        Manifest.permission.WRITE_CONTACTS,
    )

    if (contactPermissionState.status.isGranted) {
        return true
    } else {
        Column {
            val textToShow = if (contactPermissionState.status.shouldShowRationale) {
                "The WRITE_CONTACTS is important for this app. Please grant the permission."
            } else {
                "WRITE_CONTACTS permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            AlertDialogPermission(message = textToShow, permissionState = contactPermissionState)
        }
        return false
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun AlertDialogPermission(message: String, permissionState: PermissionState) {
    val openDialog = remember { mutableStateOf(false)  }

    AlertDialog(
        onDismissRequest = {
            openDialog.value = false
        },
        title = {
            Text(text = "Permission WRITE_CONTACTS")
        },
        text = {
            Text(message)
        },
        confirmButton = {
            Button(
                onClick = {
                    openDialog.value = false
                    permissionState.launchPermissionRequest()
                }) {
                Text("Request permission WRITE_CONTACTS")
            }
        }
    )
}

@Preview
@Composable
fun InfoScreenView() {

    var listP: ArrayList<PhoneModel> = ArrayList()
    var listE: ArrayList<EmailModel> = ArrayList()

    listP.add(PhoneModel("05264611500", "Mobile", 2))
    listP.add(PhoneModel("2112435", "Home", 3))

    listE.add(EmailModel("Matatov1989@gmail.com", "Work", 1))
    listE.add(EmailModel("Yura@gmail.com", "Custom", 2))

    InfoScreen(
        ContactModel(3, 1L, "Yura", "", listP, listE),
        navController = rememberNavController(),
        _mainActivity = mainActivity!!
    )
}