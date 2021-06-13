package com.android.riafytest.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "list_table")
data class ListDbModel(
    @PrimaryKey()
    val id: Int,
    val description: String?,
    val title: String?,
    val updatedAt: String?,
    val createdAt: String?,
)