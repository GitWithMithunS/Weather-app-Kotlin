package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.room.dao.CityDao
import com.example.weatherapp.data.local.room.entity.CityEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CityRepository @Inject constructor(
    private val cityDao: CityDao
) {

    //Add a new city for user
    suspend fun addCity(
        cityName: String,
        username: String,
        latitude: Double = 0.0,
        longitude: Double = 0.0
    ): Boolean {
        return try {
            val city = CityEntity(
                cityName = cityName,
                username = username,
                latitude = latitude,
                longitude = longitude
            )
            cityDao.insertCity(city)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    //Get all cities for a user
    suspend fun getCities(username: String): List<CityEntity> {
        return try {
            cityDao.getCitiesByUsername(username)
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    //Get a specific city
    suspend fun getCity(cityName: String, username: String): CityEntity? {
        return try {
            cityDao.getCityByNameAndUser(cityName, username)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    suspend fun updateCity(city: CityEntity): Boolean {
        return try {
            cityDao.updateCity(city)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    //Delete a city
    suspend fun deleteCity(city: CityEntity): Boolean {
        return try {
            cityDao.deleteCity(city)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    //Delete all cities for a user
    suspend fun deleteAllCities(username: String): Boolean {
        return try {
            cityDao.deleteCitiesByUsername(username)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}