package com.android.riafytest.database

import androidx.lifecycle.LiveData
import com.android.riafytest.model.ListDbModel
import com.android.riafytest.model.ListItemApiModel

interface AuthDatabaseSource {
    suspend fun insertAll(listDb: ListDbModel)
    suspend fun updateList(listDb: ListDbModel)
    fun getList():LiveData<List<ListDbModel>>
    suspend fun deleteList()
}