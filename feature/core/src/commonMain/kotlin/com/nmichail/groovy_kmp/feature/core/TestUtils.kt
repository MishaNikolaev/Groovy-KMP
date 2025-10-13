package com.nmichail.groovy_kmp.feature.core

import com.nmichail.groovy_kmp.core.base.state.UiState
import com.nmichail.groovy_kmp.feature.core.models.Result

object TestUtils {

    fun <T> createSuccessState(data: T): UiState<T> = UiState.Success(data)

    fun createErrorState(message: String): UiState<Nothing> = UiState.Error(message)

    fun <T> createLoadingState(): UiState<T> = UiState.Loading

    fun <T> createEmptyState(): UiState<T> = UiState.Empty

    fun <T> createSuccessResult(data: T): Result<T> = Result.Success(data)

    fun createErrorResult(message: String): Result<Nothing> = Result.Error(message = message)

    fun <T> createLoadingResult(): Result<T> = Result.Loading
}
