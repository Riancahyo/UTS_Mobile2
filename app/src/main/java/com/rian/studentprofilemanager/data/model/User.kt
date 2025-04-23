package com.rian.studentprofilemanager.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey val email: String,
    val name: String,
    val password: String,
    val phoneNumber: String,
    val address: String
)