package com.playdeadrespawn.storyapp.view.detail

import android.icu.text.SimpleDateFormat
import android.net.ParseException
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.playdeadrespawn.storyapp.R
import com.playdeadrespawn.storyapp.databinding.ActivityDetailStoryBinding
import com.playdeadrespawn.storyapp.utils.getAddress
import com.playdeadrespawn.storyapp.utils.withDateFormat
import java.util.Locale

class DetailStory : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.detail_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding()
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
        binding.tvDate.text = created?.withDateFormat()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val NAME = "name"
        const val CREATE_AT = "create_at"
        const val DESCRIPTION = "description"
        const val PHOTO_URL = "photoUrl"
        const val LONGITUDE = "lon"
        const val LATITUDE = "lat"
    }


}