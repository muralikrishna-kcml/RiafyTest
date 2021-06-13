package com.android.riafytest.network

import com.android.riafytest.model.ListApiModel
import com.android.riafytest.model.ListItemApiModel
import com.android.riafytest.model.NewItemModel
import com.android.riafytest.retrofit.ApiResult
import com.android.riafytest.retrofit.AuthService
import com.android.riafytest.util.safeApiCall
import kotlinx.coroutines.Dispatchers.IO

class AuthNetworkDataSourceImpl(private val authService: AuthService): AuthNetworkDataSource {
    override suspend fun getList(): ApiResult<ArrayList<ListItemApiModel>?> {
        return safeApiCall(IO) {
            authService.getList()
        }
    }

    override suspend fun submitNew(newItemModel: NewItemModel): ApiResult<String?> {
        return safeApiCall(IO) {
            authService.submitNew(newItemModel)
        }
    }
}