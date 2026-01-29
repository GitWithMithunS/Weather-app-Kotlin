package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.room.dao.UserDao
import com.example.weatherapp.data.local.room.entity.UserEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    /**
     * Save a new user and mark them as logged in
     */
    suspend fun saveAndLoginUser(
        username: String,
        password: String,
        city: String
    ): Boolean {
        return try {
            // Check if user already exists
            val existingUser = userDao.getUserByUsername(username)

            if (existingUser != null) {
                // Update existing user
                val updatedUser = existingUser.copy(
                    password = password,
                    defaultCity = city,
                    isLoggedIn = true
                )
                userDao.updateUser(updatedUser)
            } else {
                // Create new user
                val newUser = UserEntity(
                    username = username,
                    password = password,
                    defaultCity = city,
                    isLoggedIn = true
                )
                userDao.insertUser(newUser)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Login user with username and password
     */
    suspend fun loginUser(username: String, password: String): Boolean {
        return try {
            val user = userDao.getUserByUsername(username)

            if (user != null && user.password == password) {
                // Update user login status
                userDao.updateUser(user.copy(isLoggedIn = true))
                true
            } else {
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Get currently logged in user
     */
    suspend fun getCurrentUser(): UserEntity? {
        return try {
            userDao.getCurrentLoggedInUser()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Get user by username
     */
    suspend fun getUserByUsername(username: String): UserEntity? {
        return try {
            userDao.getUserByUsername(username)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Update user details
     */
    suspend fun updateUser(user: UserEntity): Boolean {
        return try {
            userDao.updateUser(user)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Logout current user
     */
    suspend fun logout(): Boolean {
        return try {
            userDao.logoutAllUsers()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Delete user
     */
    suspend fun deleteUser(username: String): Boolean {
        return try {
            userDao.deleteUserByUsername(username)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}