package com.android.riafytest.util

import com.android.riafytest.model.ErrorResponseModel
import com.android.riafytest.network.NetworkConstants.NETWORK_TIMEOUT
import com.android.riafytest.network.NetworkErrors.NETWORK_ERROR_TIMEOUT
import com.android.riafytest.network.NetworkErrors.NETWORK_ERROR_UNKNOWN
import com.android.riafytest.retrofit.ApiResult
import com.android.riafytest.util.GenericErrors.ERROR_UNKNOWN
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

/**
 * Reference: https://medium.com/@douglas.iacovelli/how-to-handle-errors-with-retrofit-and-coroutines-33e7492a912
 */

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T?
): ApiResult<T?> {
    return withContext(dispatcher) {
        try {
            // throws TimeoutCancellationException
            withTimeout(NETWORK_TIMEOUT){
                ApiResult.Success(apiCall.invoke())
            }
        } catch (throwable: Throwable) {
            throwable.printStackTrace()
            when (throwable) {
                is TimeoutCancellationException -> {
                    val code = 408 // timeout error code
                    Timber.e("TimeoutCancellationException ### Code : $code, ErrorResponse : Timeout")
                    ApiResult.GenericError(code, NETWORK_ERROR_TIMEOUT)
                }
                is IOException -> {
                    Timber.e("### IOException ### ${throwable.message}")
                    ApiResult.NetworkError
                }
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    Timber.e("HttpException ### Code : $code, ErrorResponse : $errorResponse")
                    val msg = Gson().fromJson(errorResponse, ErrorResponseModel::class.java)

                    if (code in 400..499 && code != 408) {
                        ApiResult.ClientError(code, msg.message)
                    } else {
                        ApiResult.GenericError(
                            code,
                            msg.message
                        )
                    }
                }
                else -> {
                    Timber.e("### NETWORK_ERROR_UNKNOWN ### ${throwable.message}")
                    ApiResult.GenericError(
                        null,
                        NETWORK_ERROR_UNKNOWN
                    )
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): String? {
    return try {
        throwable.response()?.errorBody()?.string()
    } catch (exception: Exception) {
        ERROR_UNKNOWN
    }
}
