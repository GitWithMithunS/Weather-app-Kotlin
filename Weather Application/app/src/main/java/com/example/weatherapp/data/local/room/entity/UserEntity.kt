package com.example.weatherapp.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,
    val defaultCity: String,
    val isLoggedIn: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)