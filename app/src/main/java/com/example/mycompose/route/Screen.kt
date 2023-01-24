package com.example.mycompose.route

sealed class Screen(val route: String) {
    object List: Screen(route = "list_screen/")
    object Info: Screen(route = "info_screen/{contact}")
}
