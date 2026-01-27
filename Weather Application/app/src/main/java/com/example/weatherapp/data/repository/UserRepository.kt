package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.room.dao.UserDao
import com.example.weatherapp.data.local.room.entity.UserEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    val currentUser: Flow<UserEntity?> = userDao.getLoggedInUser()

    suspend fun saveAndLoginUser(username: String, password: String, city: String) {
        val user = UserEntity(
            username = username,
            password = password,
            defaultCity = city,
            isLoggedIn = true
        )
        userDao.loginUser(user)
    }

    suspend fun getCurrentUser(): UserEntity? = currentUser.firstOrNull()

    suspend fun logout() {
        userDao.logoutAllUsers()
    }
}
