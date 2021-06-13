package com.android.riafytest.network

import com.android.riafytest.model.ListApiModel
import com.android.riafytest.model.ListItemApiModel
import com.android.riafytest.model.NewItemModel
import com.android.riafytest.retrofit.ApiResult

interface AuthNetworkDataSource {
    suspend fun getList(): ApiResult<ArrayList<ListItemApiModel>?>
    suspend fun submitNew(newItemModel: NewItemModel): ApiResult<String?>
}