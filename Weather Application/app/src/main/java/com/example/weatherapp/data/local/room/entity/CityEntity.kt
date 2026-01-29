package com.example.weatherapp.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cityName: String,
    val username: String,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val addedAt: Long = System.currentTimeMillis()
)