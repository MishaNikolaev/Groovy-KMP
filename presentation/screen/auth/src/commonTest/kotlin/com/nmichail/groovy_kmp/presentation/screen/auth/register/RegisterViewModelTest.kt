package com.nmichail.groovy_kmp.presentation.screen.auth.register

import com.nmichail.groovy_kmp.domain.models.AuthResponse
import com.nmichail.groovy_kmp.domain.models.User
import com.nmichail.groovy_kmp.domain.repository.RegisterRepository
import com.nmichail.groovy_kmp.domain.usecases.RegisterUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {
    
    private lateinit var registerUseCase: RegisterUseCase
    private lateinit var registerViewModel: RegisterViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        val mockRegisterRepository = object : RegisterRepository {
            override suspend fun register(email: String, password: String, username: String): AuthResponse {
                return when {
                    email == "newuser@example.com" && password == "password123" && username == "newuser" -> {
                        AuthResponse(
                            user = User("2", "newuser@example.com", "newuser"),
                            token = "new_user_token_456",
                            error = null
                        )
                    }
                    email == "existing@example.com" -> {
                        AuthResponse(
                            user = null,
                            token = null,
                            error = "User already exists"
                        )
                    }
                    email.isEmpty() || password.isEmpty() || username.isEmpty() -> {
                        AuthResponse(
                            user = null,
                            token = null,
                            error = "All fields are required"
                        )
                    }
                    else -> throw Exception("Network error")
                }
            }
        }
        
        registerUseCase = RegisterUseCase(mockRegisterRepository)
        registerViewModel = RegisterViewModel(registerUseCase)
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `register with valid data should succeed`() = runTest {
        var result = false
        var callbackCalled = false
        
        registerViewModel.register("newuser@example.com", "password123", "newuser") { success ->
            result = success
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertTrue(result)
        assertFalse(registerViewModel.isLoading)
        assertNull(registerViewModel.errorMessage)
    }
    
    @Test
    fun `register with existing email should fail`() = runTest {
        var result = true
        var callbackCalled = false
        
        registerViewModel.register("existing@example.com", "password123", "existinguser") { success ->
            result = success
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertFalse(result)
        assertFalse(registerViewModel.isLoading)
        assertEquals("User already exists", registerViewModel.errorMessage)
    }
    
    @Test
    fun `register with empty fields should fail`() = runTest {
        var result = true
        var callbackCalled = false
        
        registerViewModel.register("", "password123", "newuser") { success ->
            result = success
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertFalse(result)
        assertFalse(registerViewModel.isLoading)
        assertEquals("All fields are required", registerViewModel.errorMessage)
    }
    
    @Test
    fun `register should set loading state correctly`() = runTest {
        var callbackCalled = false
        
        assertFalse(registerViewModel.isLoading)
        
        registerViewModel.register("newuser@example.com", "password123", "newuser") { _ ->
            callbackCalled = true
        }
        
        assertTrue(registerViewModel.isLoading)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertFalse(registerViewModel.isLoading)
    }
    
    @Test
    fun `register should clear error message on new attempt`() = runTest {
        registerViewModel.register("existing@example.com", "password123", "existinguser") { }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertNotNull(registerViewModel.errorMessage)
        
        registerViewModel.register("newuser@example.com", "password123", "newuser") { }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertNull(registerViewModel.errorMessage)
    }
    
    @Test
    fun `register should handle network errors`() = runTest {
        var result = true
        var callbackCalled = false
        
        registerViewModel.register("networkerror@example.com", "password123", "networkuser") { success ->
            result = success
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertFalse(result)
        assertFalse(registerViewModel.isLoading)
        assertEquals("Network error", registerViewModel.errorMessage)
    }
    
    @Test
    fun `register with empty password should fail`() = runTest {
        var result = true
        var callbackCalled = false
        
        registerViewModel.register("test@example.com", "", "testuser") { success ->
            result = success
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertFalse(result)
        assertFalse(registerViewModel.isLoading)
        assertEquals("All fields are required", registerViewModel.errorMessage)
    }
    
    @Test
    fun `register with empty username should fail`() = runTest {
        var result = true
        var callbackCalled = false
        
        registerViewModel.register("test@example.com", "password123", "") { success ->
            result = success
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertFalse(result)
        assertFalse(registerViewModel.isLoading)
        assertEquals("All fields are required", registerViewModel.errorMessage)
    }
    
    @Test
    fun `register should handle exception with null message`() = runTest {
        val mockRepositoryWithNullMessage = object : RegisterRepository {
            override suspend fun register(email: String, password: String, username: String): AuthResponse {
                throw Exception()
            }
        }
        
        val registerUseCaseWithNullMessage = RegisterUseCase(mockRepositoryWithNullMessage)
        val registerViewModelWithNullMessage = RegisterViewModel(registerUseCaseWithNullMessage)
        
        var result = true
        var callbackCalled = false
        
        registerViewModelWithNullMessage.register("test@example.com", "password123", "testuser") { success ->
            result = success
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertFalse(result)
        assertFalse(registerViewModelWithNullMessage.isLoading)
        assertEquals("Network error", registerViewModelWithNullMessage.errorMessage)
    }
    
    @Test
    fun `register should handle response with null token and user`() = runTest {
        val mockRepositoryWithNullResponse = object : RegisterRepository {
            override suspend fun register(email: String, password: String, username: String): AuthResponse {
                return AuthResponse(
                    user = null,
                    token = null,
                    error = null
                )
            }
        }
        
        val registerUseCaseWithNullResponse = RegisterUseCase(mockRepositoryWithNullResponse)
        val registerViewModelWithNullResponse = RegisterViewModel(registerUseCaseWithNullResponse)
        
        var result = true
        var callbackCalled = false
        
        registerViewModelWithNullResponse.register("test@example.com", "password123", "testuser") { success ->
            result = success
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertFalse(result)
        assertFalse(registerViewModelWithNullResponse.isLoading)
        assertEquals("Unknown error", registerViewModelWithNullResponse.errorMessage)
    }
}

