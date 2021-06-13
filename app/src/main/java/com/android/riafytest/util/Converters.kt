package com.android.riafytest.util

import androidx.room.TypeConverter
import com.android.riafytest.model.ListDbModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromList(value : List<ListDbModel>) = Json.encodeToString(value)

    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<ListDbModel>>(value)
}