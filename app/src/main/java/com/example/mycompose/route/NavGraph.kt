package com.example.mycompose.route

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.mycompose.MainActivity
import com.example.mycompose.model.ContactModel
import com.example.mycompose.screen.InfoScreen
import com.example.mycompose.screen.ListScreen
import com.google.gson.Gson


@Composable
fun SetupNavGraph(navController: NavHostController, mainActivity: MainActivity) {
    NavHost(
        navController = navController,
        startDestination = Screen.List.route
    ) {
        composable(
            route = Screen.List.route,
        ) {
            ListScreen(nav_Controller = navController, _mainActivity = mainActivity)
        }
        composable(
            route = Screen.Info.route,
            arguments = listOf(navArgument("contact") {
                type = NavType.StringType
            })
        ) {
            val jsonStr = it.arguments?.getString("contact")
            val contact = Gson().fromJson(jsonStr, ContactModel::class.java)
            InfoScreen(contact = contact, navController = navController, _mainActivity = mainActivity)
        }
    }
}