package com.example.weatherapp.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val username: String,
    val password: String,
    val defaultCity: String,
    val isLoggedIn: Boolean = false
)
