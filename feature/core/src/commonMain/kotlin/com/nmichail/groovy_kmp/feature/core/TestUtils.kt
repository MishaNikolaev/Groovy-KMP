package com.nmichail.groovy_kmp.feature.core

import com.nmichail.groovy_kmp.feature.core.state.UiState
import com.nmichail.groovy_kmp.feature.core.models.Result

/**
 * Test utility functions for feature:core module
 */
object TestUtils {
    
    /**
     * Create a test success state
     */
    fun <T> createSuccessState(data: T): UiState<T> = UiState.Success(data)
    
    /**
     * Create a test error state
     */
    fun createErrorState(message: String): UiState<Nothing> = UiState.Error(message)
    
    /**
     * Create a test loading state
     */
    fun <T> createLoadingState(): UiState<T> = UiState.Loading
    
    /**
     * Create a test empty state
     */
    fun <T> createEmptyState(): UiState<T> = UiState.Empty
    
    /**
     * Create a test success result
     */
    fun <T> createSuccessResult(data: T): Result<T> = Result.Success(data)
    
    /**
     * Create a test error result
     */
    fun createErrorResult(message: String): Result<Nothing> = Result.Error(message = message)
    
    /**
     * Create a test loading result
     */
    fun <T> createLoadingResult(): Result<T> = Result.Loading
}
