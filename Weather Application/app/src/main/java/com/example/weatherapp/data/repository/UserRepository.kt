package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.room.UserDao
import com.example.weatherapp.data.local.room.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    // Register a new user
    suspend fun registerUser(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.registerUser(user)
    }

    // Find a user by email (for Login)
    suspend fun getUserByEmail(email: String): UserEntity? = withContext(Dispatchers.IO) {
        userDao.getUserByEmail(email)
    }
}