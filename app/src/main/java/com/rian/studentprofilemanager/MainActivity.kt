package com.rian.studentprofilemanager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.rian.studentprofilemanager.data.database.AppDatabase
import com.rian.studentprofilemanager.data.repository.UserRepository
import com.rian.studentprofilemanager.navigation.AppNavigation
import com.rian.studentprofilemanager.ui.theme.StudentProfileManagerTheme
import com.rian.studentprofilemanager.ui.viewmodel.AuthViewModel
import com.rian.studentprofilemanager.ui.viewmodel.AuthViewModelFactory
import com.rian.studentprofilemanager.ui.viewmodel.ProfileViewModel
import com.rian.studentprofilemanager.ui.viewmodel.ProfileViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(this)
        val repository = UserRepository(database.userDao())
        val authViewModelFactory = AuthViewModelFactory(repository)
        val profileViewModelFactory = ProfileViewModelFactory(repository)

        setContent {
            StudentProfileManagerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val authViewModel: AuthViewModel = viewModel(factory = authViewModelFactory)
                    val profileViewModel: ProfileViewModel = viewModel(factory = profileViewModelFactory)

                    AppNavigation(
                        navController = navController,
                        authViewModel = authViewModel,
                        profileViewModel = profileViewModel
                    )
                }
            }
        }
    }
}