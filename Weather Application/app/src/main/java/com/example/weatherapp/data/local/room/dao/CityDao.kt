package com.example.weatherapp.data.local.room.dao

import androidx.room.*
import com.example.weatherapp.data.local.room.entity.CityEntity

@Dao
interface CityDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCity(city: CityEntity)

    @Query("SELECT * FROM cities WHERE username = :username ORDER BY addedAt DESC")
    suspend fun getCitiesByUsername(username: String): List<CityEntity>

    @Query("SELECT * FROM cities WHERE cityName = :cityName AND username = :username LIMIT 1")
    suspend fun getCityByNameAndUser(cityName: String, username: String): CityEntity?

    @Update
    suspend fun updateCity(city: CityEntity)

    @Delete
    suspend fun deleteCity(city: CityEntity)

    @Query("DELETE FROM cities WHERE username = :username")
    suspend fun deleteCitiesByUsername(username: String)
}