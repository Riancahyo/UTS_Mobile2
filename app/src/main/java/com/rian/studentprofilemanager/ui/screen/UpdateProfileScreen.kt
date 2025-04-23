package com.rian.studentprofilemanager.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.rian.studentprofilemanager.data.model.User
import com.rian.studentprofilemanager.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateProfileScreen(
    viewModel: ProfileViewModel,
    currentUser: User,
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf(currentUser.name) }
    var phoneNumber by remember { mutableStateOf(currentUser.phoneNumber) }
    var address by remember { mutableStateOf(currentUser.address) }

    val updateState by viewModel.updateState.collectAsState()

    LaunchedEffect(updateState) {
        if (updateState is ProfileViewModel.UpdateState.Success) {
            onNavigateBack()
            viewModel.resetUpdateState()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Update Profil") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Email tidak bisa diubah (digunakan sebagai key/identifier)
            OutlinedTextField(
                value = currentUser.email,
                onValueChange = { },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email Icon") },
                modifier = Modifier.fillMaxWidth(),
                enabled = false,
                readOnly = true
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nama") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = "Nama Icon") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Nomor HP") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = "Phone Icon") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                )
            )

            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Alamat") },
                leadingIcon = { Icon(Icons.Default.Home, contentDescription = "Alamat Icon") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                )
            )

            if (updateState is ProfileViewModel.UpdateState.Error) {
                Text(
                    text = (updateState as ProfileViewModel.UpdateState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Button(
                onClick = {
                    val updatedUser = currentUser.copy(
                        name = name,
                        phoneNumber = phoneNumber,
                        address = address
                    )
                    viewModel.updateProfile(updatedUser)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                enabled = name.isNotBlank() && phoneNumber.isNotBlank() && address.isNotBlank() &&
                        updateState !is ProfileViewModel.UpdateState.Loading
            ) {
                if (updateState is ProfileViewModel.UpdateState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Update Profil")
                }
            }
        }
    }
}