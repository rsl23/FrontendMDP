//package com.example.projectmdp.data.repository
//
//import com.example.projectmdp.data.model.auth.LoginDto
//import com.example.projectmdp.data.model.auth.RegisterDto
//import com.example.projectmdp.data.source.dataclass.User
//import com.example.projectmdp.data.source.local.dao.UserDao
//import com.example.projectmdp.data.source.local.entity.UserEntity
//import com.example.projectmdp.data.source.remote.AuthApi
//import com.example.projectmdp.data.source.response.*
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.test.runTest
//import org.junit.Assert.*
//import org.junit.Before
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.Mockito.*
//import org.mockito.junit.MockitoJUnitRunner
//import org.mockito.kotlin.any
//import org.mockito.kotlin.whenever
//
//@RunWith(MockitoJUnitRunner::class)
//class AuthRepositoryTest {
//
//    @Mock
//    private lateinit var userDao: UserDao
//
//    @Mock
//    private lateinit var authApi: AuthApi
//
//    private lateinit var authRepository: AuthRepository
//
//    // Sample test data
//    private val sampleUser = User(
//        id = "1",
//        username = "testuser",
//        email = "test@example.com",
////        password = "hashedpassword",
//        created_at = "2024-01-01",
//        deleted_at = null
//    )
//
//    private val sampleUserEntity = UserEntity(
//        id = "1",
//        username = "testuser",
//        email = "test@example.com",
////        password = "hashedpassword",
//        created_at = "2024-01-01",
//        deleted_at = null
//    )
//
//    private val sampleResponseUser = com.example.projectmdp.data.source.response.user(
//        user_id = "1",
//        username = "testuser",
//        email = "test@example.com",
//        password = "hashedpassword",
//        created_at = "2024-01-01",
//        deleted_at = null
//    )
//
//    @Before
//    fun setup() {
//        authRepository = AuthRepository(userDao, authApi)
//    }
//
//    // ===== Login Tests =====
//
//    @Test
//    fun `login should return user data on successful authentication`() = runTest {
//        // Arrange
//        val loginDto = LoginDto(
//            email = "test@example.com",
//            password = "password123"
//        )
//        val authResponse = AuthResponse(
//            user = sampleResponseUser,
//            token = "jwt_token_123"
//        )
//        val apiResponse = BaseResponse<AuthResponse>(
//            message = "Login successful",
//            error = null,
//            data = authResponse
//        )
//
//        whenever(authApi.login(loginDto)).thenReturn(apiResponse)
//
//        // Act
//        val result = authRepository.login(loginDto).first()
//
//        // Assert
//        assertTrue("Login should be successful", result.isSuccess)
//        val loginResult = result.getOrNull()
//        assertNotNull("Login result should not be null", loginResult)
//        assertEquals("User ID should match", "1", loginResult!!.user.user_id)
//        assertEquals("Username should match", "testuser", loginResult.user.username)
//        assertEquals("Token should match", "jwt_token_123", loginResult.token)
//
//        // Verify user is cached locally
//        verify(userDao).insert(any<UserEntity>())
//    }
//
//    @Test
//    fun `login should return failure on invalid credentials`() = runTest {
//        // Arrange
//        val loginDto = LoginDto(
//            email = "wrong@example.com",
//            password = "wrongpassword"
//        )
//        val apiResponse = BaseResponse<AuthResponse>(
//            message = "Invalid credentials",
//            error = "Email or password is incorrect",
//            data = null
//        )
//
//        whenever(authApi.login(loginDto)).thenReturn(apiResponse)
//
//        // Act
//        val result = authRepository.login(loginDto).first()
//
//        // Assert
//        assertTrue("Login should fail", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertNotNull("Exception should not be null", exception)
//        assertTrue("Error should contain credentials message",
//            exception!!.message!!.contains("Email or password is incorrect"))
//
//        // Verify no user is cached
//        verify(userDao, never()).insert(any<UserEntity>())
//    }
//
//    @Test
//    fun `login should handle network errors gracefully`() = runTest {
//        // Arrange
//        val loginDto = LoginDto(
//            email = "test@example.com",
//            password = "password123"
//        )
//
//        whenever(authApi.login(loginDto)).thenThrow(RuntimeException("Network error"))
//
//        // Act
//        val result = authRepository.login(loginDto).first()
//
//        // Assert
//        assertTrue("Login should fail on network error", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertNotNull("Exception should not be null", exception)
//        assertTrue("Error should contain network error message",
//            exception!!.message!!.contains("Network error"))
//    }
//
//    @Test
//    fun `login should validate email format`() = runTest {
//        // Arrange
//        val invalidLoginDto = LoginDto(
//            email = "invalid-email",
//            password = "password123"
//        )
//
//        // Act
//        val result = authRepository.login(invalidLoginDto).first()
//
//        // Assert
//        assertTrue("Login should fail with invalid email", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain email validation message",
//            exception!!.message!!.contains("Invalid email format"))
//
//        // Verify API is not called for invalid input
//        verify(authApi, never()).login(any())
//    }
//
//    // ===== Register Tests =====
//
//    @Test
//    fun `register should create new user successfully`() = runTest {
//        // Arrange
//        val registerDto = RegisterDto(
//            username = "newuser",
//            email = "newuser@example.com",
//            password = "password123"
//        )
//        val authResponse = AuthResponse(
//            user = sampleResponseUser.copy(
//                username = "newuser",
//                email = "newuser@example.com"
//            ),
//            token = "jwt_token_456"
//        )
//        val apiResponse = BaseResponse<AuthResponse>(
//            message = "Registration successful",
//            error = null,
//            data = authResponse
//        )
//
//        whenever(authApi.register(registerDto)).thenReturn(apiResponse)
//
//        // Act
//        val result = authRepository.register(registerDto).first()
//
//        // Assert
//        assertTrue("Registration should be successful", result.isSuccess)
//        val registerResult = result.getOrNull()
//        assertNotNull("Register result should not be null", registerResult)
//        assertEquals("Username should match", "newuser", registerResult!!.user.username)
//        assertEquals("Email should match", "newuser@example.com", registerResult.user.email)
//
//        // Verify user is cached locally
//        verify(userDao).insert(any<UserEntity>())
//    }
//
//    @Test
//    fun `register should return failure when email already exists`() = runTest {
//        // Arrange
//        val registerDto = RegisterDto(
//            username = "testuser",
//            email = "existing@example.com",
//            password = "password123"
//        )
//        val apiResponse = BaseResponse<AuthResponse>(
//            message = "Registration failed",
//            error = "Email already exists",
//            data = null
//        )
//
//        whenever(authApi.register(registerDto)).thenReturn(apiResponse)
//
//        // Act
//        val result = authRepository.register(registerDto).first()
//
//        // Assert
//        assertTrue("Registration should fail", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain email exists message",
//            exception!!.message!!.contains("Email already exists"))
//    }
//
//    @Test
//    fun `register should validate password strength`() = runTest {
//        // Arrange
//        val weakPasswordDto = RegisterDto(
//            username = "testuser",
//            email = "test@example.com",
//            password = "123" // Too weak
//        )
//
//        // Act
//        val result = authRepository.register(weakPasswordDto).first()
//
//        // Assert
//        assertTrue("Registration should fail with weak password", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain password strength message",
//            exception!!.message!!.contains("Password must be at least"))
//
//        // Verify API is not called for invalid input
//        verify(authApi, never()).register(any())
//    }
//
//    @Test
//    fun `register should validate username uniqueness`() = runTest {
//        // Arrange
//        val registerDto = RegisterDto(
//            username = "existinguser",
//            email = "test@example.com",
//            password = "password123"
//        )
//        val apiResponse = BaseResponse<AuthResponse>(
//            message = "Registration failed",
//            error = "Username already taken",
//            data = null
//        )
//
//        whenever(authApi.register(registerDto)).thenReturn(apiResponse)
//
//        // Act
//        val result = authRepository.register(registerDto).first()
//
//        // Assert
//        assertTrue("Registration should fail", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain username taken message",
//            exception!!.message!!.contains("Username already taken"))
//    }
//
//    // ===== Logout Tests =====
//
//    @Test
//    fun `logout should clear local user data`() = runTest {
//        // Act
//        authRepository.logout()
//
//        // Assert
//        verify(userDao).clearAllUsers()
//    }
//
//    @Test
//    fun `logout should handle errors gracefully`() = runTest {
//        // Arrange
//        doThrow(RuntimeException("Database error")).whenever(userDao).clearAllUsers()
//
//        // Act & Assert - Should not throw exception
//        try {
//            authRepository.logout()
//            // Test passes if no exception is thrown
//        } catch (e: Exception) {
//            fail("Logout should handle database errors gracefully")
//        }
//    }
//
//    // ===== getCurrentUser Tests =====
//
//    @Test
//    fun `getCurrentUser should return cached user`() = runTest {
//        // Arrange
//        whenever(userDao.getCurrentUser()).thenReturn(sampleUserEntity)
//
//        // Act
//        val result = authRepository.getCurrentUser()
//
//        // Assert
//        assertNotNull("Current user should not be null", result)
//        assertEquals("User ID should match", "1", result!!.user_id)
//        assertEquals("Username should match", "testuser", result.username)
//    }
//
//    @Test
//    fun `getCurrentUser should return null when no user cached`() = runTest {
//        // Arrange
//        whenever(userDao.getCurrentUser()).thenReturn(null)
//
//        // Act
//        val result = authRepository.getCurrentUser()
//
//        // Assert
//        assertNull("Current user should be null", result)
//    }
//
//    // ===== isLoggedIn Tests =====
//
//    @Test
//    fun `isLoggedIn should return true when user exists`() = runTest {
//        // Arrange
//        whenever(userDao.getCurrentUser()).thenReturn(sampleUserEntity)
//
//        // Act
//        val result = authRepository.isLoggedIn()
//
//        // Assert
//        assertTrue("Should be logged in when user exists", result)
//    }
//
//    @Test
//    fun `isLoggedIn should return false when no user exists`() = runTest {
//        // Arrange
//        whenever(userDao.getCurrentUser()).thenReturn(null)
//
//        // Act
//        val result = authRepository.isLoggedIn()
//
//        // Assert
//        assertFalse("Should not be logged in when no user exists", result)
//    }
//
//    // ===== updateProfile Tests =====
//
//    @Test
//    fun `updateProfile should update user data successfully`() = runTest {
//        // Arrange
//        val updatedUser = sampleResponseUser.copy(username = "updateduser")
//        val userResponse = UserSingleResponse(user = updatedUser)
//        val apiResponse = BaseResponse<UserSingleResponse>(
//            message = "Profile updated",
//            error = null,
//            data = userResponse
//        )
//
//        whenever(authApi.updateProfile("1", "updateduser", null)).thenReturn(apiResponse)
//
//        // Act
//        val result = authRepository.updateProfile("1", "updateduser", null).first()
//
//        // Assert
//        assertTrue("Profile update should be successful", result.isSuccess)
//        val user = result.getOrNull()
//        assertNotNull("Updated user should not be null", user)
//        assertEquals("Username should be updated", "updateduser", user!!.username)
//
//        // Verify local cache is updated
//        verify(userDao).update(any<UserEntity>())
//    }
//
//    @Test
//    fun `updateProfile should handle validation errors`() = runTest {
//        // Arrange
//        val apiResponse = BaseResponse<UserSingleResponse>(
//            message = "Validation failed",
//            error = "Username too short",
//            data = null
//        )
//
//        whenever(authApi.updateProfile(any(), any(), any())).thenReturn(apiResponse)
//
//        // Act
//        val result = authRepository.updateProfile("1", "ab", null).first()
//
//        // Assert
//        assertTrue("Profile update should fail", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain validation message",
//            exception!!.message!!.contains("Username too short"))
//    }
//
//    // ===== changePassword Tests =====
//
//    @Test
//    fun `changePassword should update password successfully`() = runTest {
//        // Arrange
//        val apiResponse = BaseResponse<Any>(
//            message = "Password updated successfully",
//            error = null,
//            data = null
//        )
//
//        whenever(authApi.changePassword("1", "oldpass", "newpass")).thenReturn(apiResponse)
//
//        // Act
//        val result = authRepository.changePassword("1", "oldpass", "newpass").first()
//
//        // Assert
//        assertTrue("Password change should be successful", result.isSuccess)
//        assertTrue("Result should indicate success", result.getOrNull() == true)
//    }
//
//    @Test
//    fun `changePassword should validate old password`() = runTest {
//        // Arrange
//        val apiResponse = BaseResponse<Any>(
//            message = "Old password incorrect",
//            error = "Current password is incorrect",
//            data = null
//        )
//
//        whenever(authApi.changePassword("1", "wrongpass", "newpass")).thenReturn(apiResponse)
//
//        // Act
//        val result = authRepository.changePassword("1", "wrongpass", "newpass").first()
//
//        // Assert
//        assertTrue("Password change should fail", result.isFailure)
//        val exception = result.exceptionOrNull()
//        assertTrue("Error should contain old password message",
//            exception!!.message!!.contains("Current password is incorrect"))
//    }
//
//    // ===== Input Validation Tests =====
//
//    @Test
//    fun `should validate email format in register`() = runTest {
//        val invalidEmails = listOf(
//            "invalid",
//            "@domain.com",
//            "user@",
//            "user..name@domain.com",
//            ""
//        )
//
//        invalidEmails.forEach { email ->
//            val result = authRepository.register(
//                RegisterDto("user", email, "password123")
//            ).first()
//
//            assertTrue("Should fail for invalid email: $email", result.isFailure)
//        }
//    }
//
//    @Test
//    fun `should validate password requirements`() = runTest {
//        val invalidPasswords = listOf(
//            "", // Empty
//            "12", // Too short
//            "password", // No numbers
//            "12345678" // No letters
//        )
//
//        invalidPasswords.forEach { password ->
//            val result = authRepository.register(
//                RegisterDto("user", "test@example.com", password)
//            ).first()
//
//            assertTrue("Should fail for invalid password: $password", result.isFailure)
//        }
//    }
//
//    @Test
//    fun `should validate username requirements`() = runTest {
//        val invalidUsernames = listOf(
//            "", // Empty
//            "ab", // Too short
//            "user@name", // Invalid characters
//            "a".repeat(51) // Too long
//        )
//
//        invalidUsernames.forEach { username ->
//            val result = authRepository.register(
//                RegisterDto(username, "test@example.com", "password123")
//            ).first()
//
//            assertTrue("Should fail for invalid username: $username", result.isFailure)
//        }
//    }
//}
