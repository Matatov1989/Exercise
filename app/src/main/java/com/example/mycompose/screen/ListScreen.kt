package com.example.mycompose.screen

import android.net.Uri
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.mycompose.ui.theme.MyComposeTheme
import com.google.gson.Gson


private var mainActivity: MainActivity? = null
private var viewModel: ContactViewModel? = null
private var navController: NavController? = null
private var textFilter = mutableStateOf("")


@Composable
fun ListScreen(
    nav_Controller: NavController,
    _mainActivity: MainActivity
) {
    mainActivity = _mainActivity
    viewModel = _mainActivity.viewModel
    navController = nav_Controller

    viewModel?.fetchContacts(mainActivity!!, textFilter.value)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()) {

        TextFieldFilterList()

        viewModel?.listContact?.let {
            LazyColumn {
                items(viewModel!!.listContact) { contact ->
                    ContactCard(contact, navController!!)
                }
            }
        }
    }

    // close app
    BackHandler {
        mainActivity?.finish()
    }
}

@Composable
fun ContactCard(contact: ContactModel? = null, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 8.dp)
            .clickable {
                Log.d("Logs", "${contact!!.id} ${contact!!.name}")

                // pass data in json format instead of parcelable
                val json = Uri.encode(Gson().toJson(contact))

                navController.navigate(route = "info_screen/${json}")
            }
    ) {

        // avatar or first char contact's name
        if (contact?.avatar.equals("")) {
            Text(
                text = contact?.name!!.substring(0, 1),
                fontSize = 25.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        Color.Green,
                    )
            )
        } else {
            Image(
                painter = rememberAsyncImagePainter(contact!!.avatar),
                contentDescription = "Contact profile picture",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // full contact's name
        Text(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),text = contact!!.name)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextFieldFilterList() {

    // for filter a list by input characters
    OutlinedTextField(
        value = textFilter.value,
        label = { Text(text = "Enter name for filter") },
        maxLines = 1,
        onValueChange = { textForFilter ->
            textFilter.value = textForFilter
            viewModel?.fetchContacts(mainActivity?.baseContext!!, textForFilter)
        }
    )
    Text(text = "Contacts: ${viewModel?.listContact?.size}")
}


@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
    MyComposeTheme {

        var list: ArrayList<ContactModel> = ArrayList()
        var listP: ArrayList<PhoneModel> = ArrayList()
        var listE: ArrayList<EmailModel> = ArrayList()

        listP.add(PhoneModel("05264611500", "Mobile",2))
        listP.add(PhoneModel("2112435", "Home",1))

        listE.add(EmailModel("Matatov1989@gmail.com", "Mobile",1))
        listE.add(EmailModel("Yura@gmail.com", "Work",2))

        list.add(ContactModel(1,1L, "Yura", "", listP, listE))

        listP.add(PhoneModel("056844565", "Work",3))
        listE.add(EmailModel("Kotsar@gmail.com", "Home",3))

        list.add(ContactModel(2,2L, "Yulya", "", listP, listE))


        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()) {

            TextFieldFilterList()
            ListScreen(nav_Controller = rememberNavController(), mainActivity!!)

        }
    }
}