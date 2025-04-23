package com.rian.studentprofilemanager.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.rian.studentprofilemanager.data.model.User
import com.rian.studentprofilemanager.ui.screen.HomeScreen
import com.rian.studentprofilemanager.ui.screen.LoginScreen
import com.rian.studentprofilemanager.ui.screen.RegisterScreen
import com.rian.studentprofilemanager.ui.screen.UpdateProfileScreen
import com.rian.studentprofilemanager.ui.viewmodel.AuthViewModel
import com.rian.studentprofilemanager.ui.viewmodel.ProfileViewModel

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object UpdateProfile : Screen("update_profile")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel
) {
    var currentUser by remember { mutableStateOf<User?>(null) }

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = { user ->
                    currentUser = user
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            currentUser?.let { user ->
                HomeScreen(
                    viewModel = profileViewModel,
                    email = user.email,
                    onNavigateToUpdateProfile = {
                        navController.navigate(Screen.UpdateProfile.route)
                    },
                    onLogout = {
                        currentUser = null
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            } ?: run {
                LaunchedEffect(Unit) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            }
        }

        composable(Screen.UpdateProfile.route) {
            val profileState by profileViewModel.profileState.collectAsState()

            when (val state = profileState) {
                is ProfileViewModel.ProfileState.Success -> {
                    UpdateProfileScreen(
                        viewModel = profileViewModel,
                        currentUser = state.user,
                        onNavigateBack = {
                            navController.popBackStack()
                        }
                    )
                }

                else -> {
                    // If profile isn't loaded yet, use the currentUser as fallback
                    currentUser?.let { user ->
                        UpdateProfileScreen(
                            viewModel = profileViewModel,
                            currentUser = user,
                            onNavigateBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
