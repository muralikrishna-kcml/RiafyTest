package com.android.riafytest.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkerParameters
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

    var title : String = ""
    var desc : String = ""

    private val _progressBarVisibility = MutableLiveData<Boolean>()
    val progressBarVisibility : LiveData<Boolean> get() = _progressBarVisibility

    private val _showToast = MutableLiveData<Event<String>>()
    val showToast : LiveData<Event<String>> get() = _showToast

    private val _saved = MutableLiveData<Boolean>()
    val saved : LiveData<Boolean> get() = _saved

    init {
        _progressBarVisibility.value = false
        _saved.value = false
    }

    fun getList() {
        _progressBarVisibility.value = true
        viewModelScope.launch {
            val data = mainRepository.getList()
            handleResponse(data)
        }
    }

    private fun handleResponse(data: ApiResult<ArrayList<ListItemApiModel>?>) {
        _progressBarVisibility.value = false
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
        _progressBarVisibility.value = true
        viewModelScope.launch {
            val newItemModel = NewItemModel(title = title, desc = desc)
            val data = mainRepository.submitNew(newItemModel)
            handleNewResponse(data)
        }
    }

    private fun handleNewResponse(data: ApiResult<String?>) {
        _progressBarVisibility.value = false
        when (data) {
            is ApiResult.GenericError -> {
                _showToast.value = Event(data.errorMessage.toString())
            }

            is ApiResult.NetworkError -> {
                _showToast.value = Event("Unable to connect! Please check your network connection.")
            }

            is ApiResult.Success -> {
                if (data.succeeded) {
                    _saved.value = true
                    _showToast.value = Event(data.value!!)
                }
            }
            is ApiResult.ClientError -> {
                _showToast.value = Event("Bad request")
            }
        }
    }
}
