package com.playdeadrespawn.storyapp.view.detail

import android.icu.text.SimpleDateFormat
import android.net.ParseException
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.playdeadrespawn.storyapp.databinding.ActivityDetailStoryBinding
import java.util.Locale

class DetailStory : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()
        binding()
    }

    companion object {
        const val NAME = "name"
        const val CREATE_AT = "create_at"
        const val DESCRIPTION = "description"
        const val PHOTO_URL = "photoUrl"
    }

    private fun binding() {
        val photoUrl = intent.getStringExtra(PHOTO_URL)
        val name = intent.getStringExtra(NAME)
        val created = intent.getStringExtra(CREATE_AT)
        val description = intent.getStringExtra(DESCRIPTION)

        Glide.with(binding.root.context)
            .load(photoUrl)
            .into(binding.ivStory)
        binding.tvUser.text = name
        binding.tvDescription.text = description

        val oldFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.UK)
        val newFormat = SimpleDateFormat("dd MMMM yyyy", Locale.UK)

        try {
            val date = oldFormat.parse(created)
            val formattedDate = newFormat.format(date)
            binding.tvDate.text = formattedDate
        } catch (e: ParseException) {
            e.printStackTrace()
            binding.tvDate.text = created
        }
    }
}