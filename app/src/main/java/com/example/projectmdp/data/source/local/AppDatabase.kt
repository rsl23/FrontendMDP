package com.example.projectmdp.data.source.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.projectmdp.data.source.local.dao.ProductDao
import com.example.projectmdp.data.source.local.dao.TransactionDao
import com.example.projectmdp.data.source.local.dao.UserDao
import com.example.projectmdp.data.source.local.entity.ProductEntity
import com.example.projectmdp.data.source.local.entity.UserEntity

@Database(entities = [UserEntity::class, ProductEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
//    abstract fun transactionDao(): TransactionDao
    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                INSTANCE ?: Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "proyekMDP"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}