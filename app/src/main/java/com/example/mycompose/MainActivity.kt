package com.example.mycompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.mycompose.route.SetupNavGraph
import com.example.mycompose.ui.theme.MyComposeTheme
import com.google.accompanist.permissions.*


class MainActivity : ComponentActivity() {

    lateinit var navController: NavHostController
    lateinit var viewModel: ContactViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyComposeTheme {
                viewModel = ViewModelProvider(this).get(ContactViewModel::class.java)

                val isApproved = PermissionReadContacts()

                if (isApproved) {
                    navController = rememberNavController()
                    SetupNavGraph(navController = navController, this)
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionReadContacts(): Boolean {

    val contactPermissionState = rememberPermissionState(
        android.Manifest.permission.READ_CONTACTS,
    )

    if (contactPermissionState.status.isGranted) {
        return true
    } else {
        Column {
            val textToShow = if (contactPermissionState.status.shouldShowRationale) {
                // If the user has denied the permission but the rationale can be shown,
                // then gently explain why the app requires this permission
                "The READ_CONTACTS is important for this app. Please grant the permission."
            } else {
                // If it's the first time the user lands on this feature, or the user
                // doesn't want to be asked again for this permission, explain that the
                // permission is required
                "READ_CONTACTS permission required for this feature to be available. " +
                        "Please grant the permission"
            }
            Text(textToShow)
            Button(onClick = { contactPermissionState.launchPermissionRequest() }) {
                Text("Request permission READ_CONTACTS")
            }
        }
        return false
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyComposeTheme {
        PermissionReadContacts()
    }
}