package com.nmichail.groovy_kmp.presentation.screen.auth.login

import com.nmichail.groovy_kmp.domain.models.AuthResponse
import com.nmichail.groovy_kmp.domain.models.User
import com.nmichail.groovy_kmp.domain.repository.AuthRepository
import com.nmichail.groovy_kmp.domain.usecases.LoginUseCase
import com.nmichail.groovy_kmp.presentation.session.SessionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {
    
    private class TestSessionViewModel : SessionViewModel() {
        private var savedUser: User? = null
        private var savedToken: String? = null
        
        fun setSavedSession(user: User?, token: String?) {
            savedUser = user
            savedToken = token
        }
        
        override fun requestSessionLoad() {
            onLoadSession?.invoke(savedUser, savedToken)
        }
    }
    
    private lateinit var loginUseCase: LoginUseCase
    private lateinit var sessionViewModel: SessionViewModel
    private lateinit var loginViewModel: LoginViewModel
    private val testDispatcher = StandardTestDispatcher()
    
    @BeforeTest
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        val mockAuthRepository = object : AuthRepository {
            override suspend fun login(email: String, password: String): AuthResponse {
                return when {
                    email == "test@example.com" && password == "password123" -> {
                        AuthResponse(
                            user = User("1", "test@example.com", "testuser"),
                            token = "valid_token_123",
                            error = null
                        )
                    }
                    email == "invalid@example.com" -> {
                        AuthResponse(
                            user = null,
                            token = null,
                            error = "Invalid credentials"
                        )
                    }
                    else -> throw Exception("Network error")
                }
            }
            
            override suspend fun register(email: String, password: String, username: String): AuthResponse {
                throw NotImplementedError("Register not implemented in this mock")
            }
            
            override suspend fun logout(): Boolean {
                return true
            }
        }
        
        loginUseCase = LoginUseCase(mockAuthRepository)
        sessionViewModel = TestSessionViewModel()
        loginViewModel = LoginViewModel(loginUseCase, sessionViewModel)
    }
    
    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `login with valid credentials should succeed`() = runTest {
        var result = false
        var callbackCalled = false
        
        loginViewModel.login("test@example.com", "password123") { success ->
            result = success
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertTrue(result)
        assertFalse(loginViewModel.isLoading)
        assertNull(loginViewModel.errorMessage)
        assertNotNull(loginViewModel.getUser())
        assertNotNull(loginViewModel.getToken())
    }
    
    @Test
    fun `login with invalid credentials should fail`() = runTest {
        var result = true
        var callbackCalled = false
        
        loginViewModel.login("invalid@example.com", "wrongpassword") { success ->
            result = success
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertFalse(result)
        assertFalse(loginViewModel.isLoading)
        assertEquals("Invalid credentials", loginViewModel.errorMessage)
        assertNull(loginViewModel.getUser())
        assertNull(loginViewModel.getToken())
    }
    
    @Test
    fun `login should set loading state correctly`() = runTest {
        var callbackCalled = false
        
        assertFalse(loginViewModel.isLoading)
        
        loginViewModel.login("test@example.com", "password123") { _ ->
            callbackCalled = true
        }

        assertTrue(loginViewModel.isLoading)
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertFalse(loginViewModel.isLoading)
    }
    
    @Test
    fun `login should clear error message on new attempt`() = runTest {
        loginViewModel.login("invalid@example.com", "wrongpassword") { }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertNotNull(loginViewModel.errorMessage)
        
        loginViewModel.login("test@example.com", "password123") { }
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertNull(loginViewModel.errorMessage)
    }
    
    @Test
    fun `login should handle network errors`() = runTest {
        var result = true
        var callbackCalled = false
        
        loginViewModel.login("nonexistent@example.com", "password123") { success ->
            result = success
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertFalse(result)
        assertFalse(loginViewModel.isLoading)
        assertEquals("Network error", loginViewModel.errorMessage)
    }
    
    @Test
    fun `checkSavedSession should return true when session exists`() = runTest {
        val testUser = User("1", "test@example.com", "testuser")
        val testToken = "test_token"
        
        val authResponse = AuthResponse(user = testUser, token = testToken, error = null)
        sessionViewModel.saveSession(authResponse)
        
        (sessionViewModel as TestSessionViewModel).setSavedSession(testUser, testToken)
        
        var result = false
        var callbackCalled = false
        
        loginViewModel.checkSavedSession { hasSession ->
            result = hasSession
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertTrue(result)
        assertEquals(testUser, loginViewModel.getUser())
        assertEquals(testToken, loginViewModel.getToken())
    }
    
    @Test
    fun `checkSavedSession should return false when no session exists`() = runTest {
        sessionViewModel.clearSession()
        
        (sessionViewModel as TestSessionViewModel).setSavedSession(null, null)
        
        var result = true
        var callbackCalled = false
        
        loginViewModel.checkSavedSession { hasSession ->
            result = hasSession
            callbackCalled = true
        }
        
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertTrue(callbackCalled)
        assertFalse(result)
        assertNull(loginViewModel.getUser())
        assertNull(loginViewModel.getToken())
    }
    
    @Test
    fun `clearSession should clear all session data`() = runTest {
        val testUser = User("1", "test@example.com", "testuser")
        val testToken = "test_token"
        
        val authResponse = AuthResponse(user = testUser, token = testToken, error = null)
        sessionViewModel.saveSession(authResponse)
        
        assertEquals(testUser, loginViewModel.getUser())
        assertEquals(testToken, loginViewModel.getToken())
        
        loginViewModel.clearSession()
        
        assertNull(loginViewModel.getUser())
        assertNull(loginViewModel.getToken())
    }
    
    @Test
    fun `getUser should return session user when available`() = runTest {
        val testUser = User("1", "test@example.com", "testuser")
        val testToken = "test_token"
        
        val authResponse = AuthResponse(user = testUser, token = testToken, error = null)
        sessionViewModel.saveSession(authResponse)
        
        assertEquals(testUser, loginViewModel.getUser())
    }
    
    @Test
    fun `getToken should return session token when available`() = runTest {
        val testUser = User("1", "test@example.com", "testuser")
        val testToken = "test_token"
        
        val authResponse = AuthResponse(user = testUser, token = testToken, error = null)
        sessionViewModel.saveSession(authResponse)
        
        assertEquals(testToken, loginViewModel.getToken())
    }
}

