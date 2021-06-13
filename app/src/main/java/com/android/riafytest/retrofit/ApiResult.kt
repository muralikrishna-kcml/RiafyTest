package com.android.riafytest.retrofit

sealed class ApiResult<out T> {

    data class Success<out T>(val value: T): ApiResult<T>()

    data class GenericError(
        val code: Int? = null,
        val errorMessage: String? = null
    ): ApiResult<Nothing>()

    data class ClientError(
        val code: Int? = null,
        val errorMessage: String? = null
    ): ApiResult<Nothing>()

    object NetworkError: ApiResult<Nothing>()
}


/**
 * `true` if [Result] is of type [Success] & holds non-null [Success.data].
 */
val ApiResult<*>.succeeded
    get() = this is ApiResult.Success && value != null
