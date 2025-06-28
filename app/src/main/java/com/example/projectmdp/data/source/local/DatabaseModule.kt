package com.example.projectmdp.data.source.local

import android.content.Context
import androidx.room.Room
import com.example.projectmdp.data.source.local.dao.ProductDao
import com.example.projectmdp.data.source.local.dao.TransactionDao
import com.example.projectmdp.data.source.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "proyekMDP"
        ).fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    // Kalau nanti kamu butuh UserDao juga:
     @Provides
     fun provideUserDao(database: AppDatabase): UserDao {
         return database.userDao()
     }

    @Provides
    fun provideTransactionDao(database: AppDatabase): TransactionDao {
        return database.transactionDao()
    }

    @Provides
    @Singleton
    fun provideSessionManager(@ApplicationContext context: Context): SessionManager {
        return SessionManager(context)
    }
}