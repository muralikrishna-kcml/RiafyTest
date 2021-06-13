package com.android.riafytest.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponseModel (
    val message: String?
)