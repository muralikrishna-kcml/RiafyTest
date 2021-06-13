package com.android.riafytest.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class NewItemModel (
    val title : String,
    val desc : String
)