package com.rian.studentprofilemanager.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rian.studentprofilemanager.data.model.User
import com.rian.studentprofilemanager.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(private val repository: UserRepository) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
    val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()

    fun getProfile(email: String) {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            val result = repository.getUserByEmail(email)
            _profileState.value = result.fold(
                onSuccess = { ProfileState.Success(it) },
                onFailure = { ProfileState.Error(it.message ?: "Gagal memuat profil") }
            )
        }
    }

    fun updateProfile(user: User) {
        viewModelScope.launch {
            _updateState.value = UpdateState.Loading
            val result = repository.updateUser(user)
            _updateState.value = result.fold(
                onSuccess = { UpdateState.Success },
                onFailure = { UpdateState.Error(it.message ?: "Gagal memperbarui profil") }
            )
            // Refresh profile state
            getProfile(user.email)
        }
    }

    fun resetUpdateState() {
        _updateState.value = UpdateState.Idle
    }

    sealed class ProfileState {
        object Idle : ProfileState()
        object Loading : ProfileState()
        data class Success(val user: User) : ProfileState()
        data class Error(val message: String) : ProfileState()
    }

    sealed class UpdateState {
        object Idle : UpdateState()
        object Loading : UpdateState()
        object Success : UpdateState()
        data class Error(val message: String) : UpdateState()
    }
}

class ProfileViewModelFactory(private val repository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}