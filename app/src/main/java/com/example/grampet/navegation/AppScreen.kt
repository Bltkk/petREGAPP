package com.example.grampet.navegation

sealed class AppScreens(val route: String) {
    object Login: AppScreens("login_screen")
    object Register: AppScreens("register_screen")
    object Main: AppScreens("main_screen") // Esta será tu "SecondScreen" o Dashboard
}
