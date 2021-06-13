package com.android.riafytest.database

import androidx.lifecycle.LiveData
import com.android.riafytest.dao.ListDao
import com.android.riafytest.model.ListDbModel
import com.android.riafytest.model.ListItemApiModel
import timber.log.Timber

class AuthDataSourceImpl(private val listDao: ListDao): AuthDatabaseSource {
    override suspend fun insertAll(listDb: ListDbModel) {
        listDao.insertAll(listDb)
    }

    override fun insert(listDb: ListDbModel) {
        Timber.e("insert")
        listDao.insert(listDb)
    }

    override suspend fun updateList(listDb: ListDbModel) {
        listDao.updateList(listDb)
    }

    override fun getList(): LiveData<List<ListDbModel>> {
        return listDao.getList()
    }

    override suspend fun deleteList() {

        listDao.deleteList()
    }


}