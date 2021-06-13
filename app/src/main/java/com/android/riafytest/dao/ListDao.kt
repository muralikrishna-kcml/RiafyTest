package com.android.riafytest.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.android.riafytest.model.ListDbModel

@Dao
interface ListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(listDb: ListDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(listDb: ListDbModel)

    @Update
    suspend fun updateList(listDb: ListDbModel)

    @Query("SELECT * FROM list_table ORDER BY id DESC")
    fun getList(): LiveData<List<ListDbModel>>

    @Query("DELETE FROM list_table")
    suspend fun deleteList()
}