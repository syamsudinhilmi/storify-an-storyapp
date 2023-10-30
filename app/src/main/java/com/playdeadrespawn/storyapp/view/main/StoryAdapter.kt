package com.playdeadrespawn.storyapp.view.main

import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.net.ParseException
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.playdeadrespawn.storyapp.data.response.ListStoryItem
import com.playdeadrespawn.storyapp.databinding.ItemStoryBinding
import com.playdeadrespawn.storyapp.view.detail.DetailStory
import java.util.Locale


class StoryAdapter(private val listStory: ArrayList<ListStoryItem>): RecyclerView.Adapter<StoryAdapter.StoryViewHolder>() {

    class StoryViewHolder(private val binding: ItemStoryBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ListStoryItem) {
            Glide.with(binding.root.context)
                .load(data.photoUrl)
                .into(binding.ivStory)

            binding.tvUser.text = data.name
            binding.tvDescription.text = data.description
            val oldFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.UK)
            val newFormat = SimpleDateFormat("dd MMMM yyyy", Locale.UK)

            try {
                val date = oldFormat.parse(data.createdAt)
                val formattedDate = newFormat.format(date)
                binding.tvDate.text = formattedDate
            } catch (e: ParseException) {
                e.printStackTrace()
                binding.tvDate.text = data.createdAt
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailStory::class.java)
                intent.putExtra(DetailStory.NAME, data.name)
                intent.putExtra(DetailStory.CREATE_AT, data.createdAt)
                intent.putExtra(DetailStory.DESCRIPTION, data.description)
                intent.putExtra(DetailStory.PHOTO_URL, data.photoUrl)
                itemView.context.startActivity(intent)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoryViewHolder(view)
    }
    override fun getItemCount(): Int = listStory.size

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        holder.bind(listStory[position])
    }
}
