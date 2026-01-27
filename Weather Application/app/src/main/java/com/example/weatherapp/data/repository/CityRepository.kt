package com.example.weatherapp.data.repository

import com.example.weatherapp.data.local.room.dao.CityDao
import com.example.weatherapp.data.local.room.entity.CityEntity
import javax.inject.Inject

class CityRepository @Inject constructor(
    private val cityDao: CityDao
) {

    suspend fun getCities(username: String): List<CityEntity> {
        return cityDao.getCities(username)
    }

    suspend fun addCity(cityName: String, username: String) {
        cityDao.insertCity(
            CityEntity(
                cityName = cityName,
                username = username
            )
        )
    }

    suspend fun deleteCity(city: CityEntity) {
        cityDao.deleteCity(city)
    }
}
