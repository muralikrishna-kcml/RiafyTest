package com.android.riafytest.repository

import androidx.lifecycle.LiveData
import com.android.riafytest.database.AuthDatabaseSource
import com.android.riafytest.model.*
import com.android.riafytest.network.AuthNetworkDataSource
import com.android.riafytest.retrofit.ApiResult
import com.android.riafytest.retrofit.succeeded

class MainRepositoryImpl(
    private val authNetworkDataSource: AuthNetworkDataSource,
    private val authDatabaseSource: AuthDatabaseSource
) :
    MainRepository {
    override suspend fun getList(): ApiResult<ArrayList<ListItemApiModel>?> {
        val response = authNetworkDataSource.getList()
//        if(response is ApiResult.Success && response.succeeded){
//            authDatabaseSource.insertAll(response.value?)
//        }
        return response
    }

    override suspend fun insertAll(listDb: ListDbModel) {
        authDatabaseSource.insertAll(listDb)
    }

    override fun getDbList(): LiveData<List<ListDbModel>> {
        return authDatabaseSource.getList()
    }

    override suspend fun submitNew(newItemModel: NewItemModel): ApiResult<String?> {
        return authNetworkDataSource.submitNew(newItemModel)
    }

    override suspend fun deleteList() {
        authDatabaseSource.deleteList()
    }
}