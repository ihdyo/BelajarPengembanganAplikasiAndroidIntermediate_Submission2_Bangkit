package com.ihdyo.postit.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.R
import com.ihdyo.postit.databinding.ItemStoryBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class StoryAdapter : PagingDataAdapter<DataDetail, StoryAdapter.ListViewHolder>(StoryDetailDiffCallback()) {
    interface OnItemClickCallback { fun onItemClicked(data: DataDetail) }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) { this.onItemClickCallback = onItemClickCallback }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item)
            holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(item) }
        }
    }

    class ListViewHolder(private var binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @RequiresApi(Build.VERSION_CODES.O)
        fun bind(data: DataDetail) {
            binding.authorStory.text = data.name
            binding.dateStory.text = formatDateToString(data.createdAt.toString())
            Glide.with(itemView.context).load(data.photoUrl).error(R.drawable.ic_launcher_foreground).into(binding.imageStory)
        }
    }

    class StoryDetailDiffCallback : DiffUtil.ItemCallback<DataDetail>() {
        override fun areItemsTheSame(oldItem: DataDetail, newItem: DataDetail): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataDetail, newItem: DataDetail): Boolean { return oldItem == newItem }
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        @JvmStatic
        fun formatDateToString(dateString: String): String {
            val inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm.SSS'Z'", Locale.getDefault())
            val outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.getDefault())

            val instant = Instant.parse(dateString)
            val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())

            return outputFormatter.format(localDateTime)
        }

        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataDetail>() {
            override fun areItemsTheSame(oldItem: DataDetail, newItem: DataDetail): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DataDetail, newItem: DataDetail): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}