package com.android.riafytest.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ListItemApiModel(
    val description: String,
    val id: Int,
    val title: String,
    @Json(name = "created_at")
    val createdAt: String,
    @Json(name = "updated_at")
    val updatedAt: String
)

fun ListItemApiModel.toListDbModel(): ListDbModel {
    return ListDbModel(
        id = this.id,
        title = this.title,
        description = this.description,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}