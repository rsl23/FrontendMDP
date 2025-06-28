package com.example.projectmdp.data.source.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.projectmdp.data.source.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    // Insert with conflict strategy untuk handle duplicate data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Delete
    suspend fun deleteUser(user: UserEntity)

    // Basic queries dengan nullable result
    @Query("SELECT * FROM users WHERE id = :id AND deleted_at IS NULL")
    suspend fun getUserById(id: String): UserEntity?
    
    @Query("SELECT * FROM users WHERE id = :id AND deleted_at IS NULL")
    fun getUserByIdFlow(id: String): Flow<UserEntity?>
    
    // Get current logged user (main user yang login)
    @Query("SELECT * FROM users WHERE role = 'current_user' AND deleted_at IS NULL LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?
    
    @Query("SELECT * FROM users WHERE role = 'current_user' AND deleted_at IS NULL LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>
    
    // Search users untuk chat/contact purposes
    @Query("SELECT * FROM users WHERE username LIKE '%' || :query || '%' OR email LIKE '%' || :query || '%' AND deleted_at IS NULL")
    suspend fun searchUsers(query: String): List<UserEntity>
    
    @Query("SELECT * FROM users WHERE email = :email AND deleted_at IS NULL")
    suspend fun getUserByEmail(email: String): UserEntity?
    
    // Get recently contacted users
    @Query("SELECT * FROM users WHERE role != 'current_user' AND deleted_at IS NULL ORDER BY last_updated DESC LIMIT :limit")
    suspend fun getRecentUsers(limit: Int = 10): List<UserEntity>
    
    // Cache management
    @Query("DELETE FROM users WHERE role != 'current_user'")
    suspend fun clearCachedUsers()
    
    @Query("DELETE FROM users")
    suspend fun clearAllUsers()
    
    @Query("SELECT COUNT(*) FROM users WHERE deleted_at IS NULL")
    suspend fun getUserCount(): Int
    
    // Update specific fields untuk current user
    @Query("UPDATE users SET username = :username, last_updated = :timestamp WHERE id = :id")
    suspend fun updateUsername(id: String, username: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE users SET address = :address, last_updated = :timestamp WHERE id = :id")
    suspend fun updateAddress(id: String, address: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE users SET phone_number = :phoneNumber, last_updated = :timestamp WHERE id = :id")
    suspend fun updatePhoneNumber(id: String, phoneNumber: String, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE users SET profile_picture = :profilePicture, last_updated = :timestamp WHERE id = :id")
    suspend fun updateProfilePicture(id: String, profilePicture: String, timestamp: Long = System.currentTimeMillis())
    
    // Soft delete
    @Query("UPDATE users SET deleted_at = :deletedAt WHERE id = :id")
    suspend fun markAsDeleted(id: String, deletedAt: String)
    
    // Get all cached users (untuk contact list)
    @Query("SELECT * FROM users WHERE deleted_at IS NULL ORDER BY username ASC")
    suspend fun getAllCachedUsers(): List<UserEntity>
}