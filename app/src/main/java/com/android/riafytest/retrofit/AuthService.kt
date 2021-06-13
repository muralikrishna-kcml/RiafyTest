package com.android.riafytest.retrofit

import com.android.riafytest.model.ListApiModel
import com.android.riafytest.model.ListItemApiModel
import com.android.riafytest.model.NewItemModel
import com.android.riafytest.util.BASE_URL
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

interface AuthService {

    @GET("kotlintest")
    suspend fun getList(): ArrayList<ListItemApiModel>

    @POST("kotlintest")
    suspend fun submitNew(@Body() newItem : NewItemModel): String

    companion object {
        fun create(): AuthService {
            val logger = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AuthService::class.java)
        }
    }
}