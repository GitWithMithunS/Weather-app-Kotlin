package com.example.weatherapp.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.weatherapp.data.local.room.dao.CityDao
import com.example.weatherapp.data.local.room.dao.UserDao
import com.example.weatherapp.data.local.room.entity.CityEntity
import com.example.weatherapp.data.local.room.entity.UserEntity

@Database(
    entities = [UserEntity::class, CityEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun cityDao(): CityDao
}
