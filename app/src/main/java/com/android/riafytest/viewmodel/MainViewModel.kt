package com.android.riafytest.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.riafytest.model.ListDbModel
import com.android.riafytest.model.ListItemApiModel
import com.android.riafytest.model.NewItemModel
import com.android.riafytest.model.toListDbModel
import com.android.riafytest.repository.MainRepository
import com.android.riafytest.retrofit.ApiResult
import com.android.riafytest.retrofit.succeeded
import com.android.riafytest.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val mainRepository: MainRepository) : ViewModel() {

    var      title : String = ""
    var desc : String = ""

    private val _showToast = MutableLiveData<Event<String>>()
    val showToast : LiveData<Event<String>> get() = _showToast

    fun getList() {
        viewModelScope.launch {
            val data = mainRepository.getList()
            handleResponse(data)
        }
    }

    private fun handleResponse(data: ApiResult<ArrayList<ListItemApiModel>?>) {
        when (data) {
            is ApiResult.GenericError -> {
                _showToast.value = Event(data.errorMessage.toString())
            }

            is ApiResult.NetworkError -> {
                _showToast.value = Event("Unable to connect! Please check your network connection.")
            }

            is ApiResult.Success -> {
                if (data.succeeded) {
                    viewModelScope.launch {
                        for (l in data.value?.iterator()!!) {
                            mainRepository.insertAll(l.toListDbModel())
                        }
                    }
                }
            }
            is ApiResult.ClientError -> {
                _showToast.value = Event("Bad request")
            }
        }
    }

    fun submitNew() {
        viewModelScope.launch {
            val newItemModel = NewItemModel(title = title, desc = desc)
            val data = mainRepository.submitNew(newItemModel)
            handleNewResponse(data)
        }
    }

    private fun handleNewResponse(data: ApiResult<String?>) {
        when (data) {
            is ApiResult.GenericError -> {
                _showToast.value = Event(data.errorMessage.toString())
            }

            is ApiResult.NetworkError -> {
                _showToast.value = Event("Unable to connect! Please check your network connection.")
            }

            is ApiResult.Success -> {
                if (data.succeeded) {
                    viewModelScope.launch {
                        val newItemModel = ListItemApiModel(id = 100, title = title, description = desc, createdAt = System.currentTimeMillis().toString(), updatedAt = "")
                        mainRepository.insertAll(newItemModel.toListDbModel())
                    }
                    _showToast.value = Event(data.value!!)
                }
            }
            is ApiResult.ClientError -> {
                _showToast.value = Event("Bad request")
            }
        }
    }
}