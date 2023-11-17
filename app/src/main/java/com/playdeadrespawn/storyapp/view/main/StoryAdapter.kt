package com.playdeadrespawn.storyapp.view.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playdeadrespawn.storyapp.data.response.ListStoryItem
import com.playdeadrespawn.storyapp.databinding.ItemStoryBinding
import com.playdeadrespawn.storyapp.utils.getAddress
import com.playdeadrespawn.storyapp.utils.withDateFormat
import com.playdeadrespawn.storyapp.view.detail.DetailStory


class StoryAdapter: PagingDataAdapter<ListStoryItem, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    class StoryViewHolder(private val binding: ItemStoryBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(data.photoUrl)
                .into(binding.ivStory)

            binding.tvUser.text = data.name
            binding.tvDescription.text = data.description
            binding.tvDate.text = data.createdAt.withDateFormat()

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStory::class.java)
                intent.putExtra(DetailStory.NAME, data.name)
                intent.putExtra(DetailStory.CREATE_AT, data.createdAt)
                intent.putExtra(DetailStory.DESCRIPTION, data.description)
                intent.putExtra(DetailStory.PHOTO_URL, data.photoUrl)
                intent.putExtra(DetailStory.LONGITUDE, data.lon.toString())
                intent.putExtra(DetailStory.LATITUDE, data.lat.toString())
                itemView.context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<ListStoryItem>() {
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}
