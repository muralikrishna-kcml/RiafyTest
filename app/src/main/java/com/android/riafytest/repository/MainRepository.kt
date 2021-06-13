package com.android.riafytest.repository

import androidx.lifecycle.LiveData
import com.android.riafytest.model.*
import com.android.riafytest.retrofit.ApiResult

interface MainRepository {
    suspend fun getList(): ApiResult<ArrayList<ListItemApiModel>?>
    suspend fun insertAll(listDb: ListDbModel)
    fun getDbList(): LiveData<List<ListDbModel>>
    suspend fun submitNew(newItemModel: NewItemModel): ApiResult<String?>
    suspend fun deleteList()
}