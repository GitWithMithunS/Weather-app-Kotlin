package com.example.weatherapp.di

import com.example.weatherapp.data.repository.CityRepository
import com.example.weatherapp.data.repository.UserRepository
import com.example.weatherapp.data.repository.WeatherRepository
import com.example.weatherapp.data.local.room.dao.CityDao
import com.example.weatherapp.data.local.room.dao.UserDao
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.Provides
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(
        userDao: UserDao
    ): UserRepository = UserRepository(userDao)

    @Provides
    @Singleton
    fun provideCityRepository(
        cityDao: CityDao
    ): CityRepository = CityRepository(cityDao)

    @Provides
    @Singleton
    fun provideWeatherRepository(): WeatherRepository =
        WeatherRepository()
}
