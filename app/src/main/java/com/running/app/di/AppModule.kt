   package com.running.app.di

import android.content.Context
import com.running.app.core.database.AppDatabase
import com.running.app.core.database.RunDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

   @Module
   @InstallIn(SingletonComponent::class)
   object AppModule {

       @Provides
       @Singleton
       fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
           AppDatabase.getInstance(context)

       @Provides
       @Singleton
       fun provideRunDao(db: AppDatabase): RunDao = db.runDao()
   }
