package com.android.riafytest.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.work.*
import com.android.riafytest.R
import com.android.riafytest.adapter.ListRVAdapter
import com.android.riafytest.database.AuthDatabaseSource
import com.android.riafytest.databinding.ActivityMainBinding
import com.android.riafytest.model.ListDbModel
import com.android.riafytest.model.ListItemApiModel
import com.android.riafytest.model.toListDbModel
import com.android.riafytest.repository.MainRepository
import com.android.riafytest.util.EventObserver
import com.android.riafytest.viewmodel.MainViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

var titl : String = ""
var des : String = ""

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding?= null
    private val binding get() = _binding!!
    private lateinit var adapter : ListRVAdapter
    private var rv : RecyclerView?= null
    private val viewModel : MainViewModel by viewModels()
    val request = OneTimeWorkRequestBuilder<MyWorker>().build()
    @Inject
    lateinit var mainRepository: MainRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getList()
        setRecyclerView()
        setOnClickListener()
        subscribeObservers()
    }

    private fun subscribeObservers() {
        viewModel.showToast.observe(this, EventObserver {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        viewModel.progressBarVisibility.observe(this, Observer {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
            binding.view.visibility = if (it) View.VISIBLE else View.GONE
        })

        viewModel.saved.observe(this, Observer {
            WorkManager.getInstance(this).enqueue(request)
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(request.id)
                .observe(this, Observer {

                    val status: String = it.state.name
                    Toast.makeText(this, status, Toast.LENGTH_SHORT).show()
                })
        })

    }

    private fun setOnClickListener() {
        binding.fab.setOnClickListener {
            showDialog()
        }

        binding.refreshView.setOnRefreshListener(OnRefreshListener { // Load data to your RecyclerView
            var list : List<ListDbModel> ?= null
            mainRepository.getDbList().observe(this, Observer {
                list = it
                adapter.submitList(list = list as ArrayList<ListDbModel>)
                adapter.notifyDataSetChanged()
                binding.refreshView.isRefreshing = false
            })
        })
    }

    private fun setRecyclerView() {
        var list : List<ListDbModel> ?= null
        rv = binding.rv
        rv?.layoutManager = LinearLayoutManager(this)
        adapter = ListRVAdapter()
        rv?.adapter = adapter
        mainRepository.getDbList().observe(this, Observer {
            list = it
            adapter.submitList(list = list as ArrayList<ListDbModel>)
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        binding.rv.adapter = null
        _binding = null
        rv = null
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.alert_dialog)
        dialog.findViewById<MaterialButton>(R.id.mbtSubmit).setOnClickListener {
            titl = dialog.findViewById<EditText>(R.id.etTitle).text.toString()
            des = dialog.findViewById<EditText>(R.id.etDesc).text.toString()
            viewModel.title = titl
            viewModel.desc = des
            if (viewModel.title.isNotEmpty() && viewModel.desc.isNotEmpty()) {
                viewModel.submitNew()
                dialog.dismiss()
            }
        }
        dialog.show()
    }
}

class MyWorker(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {

    @Inject
    lateinit var authDatabaseSource: AuthDatabaseSource

    override fun doWork(): Result {
        return try {
            val newItemModel = ListItemApiModel(
                id = 100,
                title = titl,
                description = des,
                createdAt = System.currentTimeMillis().toString(),
                updatedAt = ""
            )
            authDatabaseSource.insert(newItemModel.toListDbModel())

            Result.success()
        } catch (throwable: Throwable) {
            Timber.e(throwable, "Error applying blur")
            Result.failure()
        }
    }
}