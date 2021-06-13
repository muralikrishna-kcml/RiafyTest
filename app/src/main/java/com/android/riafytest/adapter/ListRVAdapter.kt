package com.android.riafytest.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.riafytest.adapter.ListRVAdapter.ViewHolder
import com.android.riafytest.databinding.ItemListBinding
import com.android.riafytest.model.ListDbModel

class ListRVAdapter : RecyclerView.Adapter<ViewHolder>() {

    private var localList : ArrayList<ListDbModel> ?= null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = localList?.get(position)
        holder.bind(item!!)
    }

    fun submitList(list: ArrayList<ListDbModel>?){
        localList = list
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return localList?.size ?: 0
    }

    class ViewHolder(private val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root){

        private val titleTv = binding.tvTitle
        private val descTv = binding.tvDescription

        fun bind(item: ListDbModel) {
           titleTv.text = item.title
            descTv.text = item.description
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemListBinding.inflate(inflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

}