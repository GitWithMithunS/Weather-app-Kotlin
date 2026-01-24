package com.example.weatherapp.data.local.room.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.weatherapp.data.local.room.entity.CityEntity

@Dao
interface CityDao {

    @Query("SELECT * FROM cities WHERE username = :username")
    suspend fun getCities(username: String): List<CityEntity>

    @Insert
    suspend fun insertCity(city: CityEntity)

    @Delete
    suspend fun deleteCity(city: CityEntity)
}
