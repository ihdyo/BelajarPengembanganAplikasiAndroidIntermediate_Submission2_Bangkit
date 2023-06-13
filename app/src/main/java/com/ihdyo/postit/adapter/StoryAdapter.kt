package com.ihdyo.postit.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ihdyo.postit.data.db.DataDetail
import com.ihdyo.postit.R
import com.ihdyo.postit.databinding.PostItemBinding
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class StoryAdapter : PagingDataAdapter<DataDetail, StoryAdapter.ListViewHolder>(StoryDetailDiffCallback()) {
    interface OnItemClickCallback { fun onItemClicked(data: DataDetail) }

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) { this.onItemClickCallback = onItemClickCallback }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item)
            holder.itemView.setOnClickListener { onItemClickCallback.onItemClicked(item) }
        }
    }

    class ListViewHolder(private var binding: PostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
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
        @JvmStatic
        fun formatDateToString(dateString: String): String {
            val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm.SSS'Z'", Locale.getDefault())
            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val date: Date?
            var outputDate = ""

            try {
                date = inputDateFormat.parse(dateString)
                outputDate = outputDateFormat.format(date!!)
            } catch (e: ParseException) { e.printStackTrace() }

            return outputDate
        }
    }
}