package com.example.weatherapp.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cities")
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val username: String
)
