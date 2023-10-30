package com.playdeadrespawn.storyapp.view.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.playdeadrespawn.storyapp.R
import com.playdeadrespawn.storyapp.data.pref.UserPreference
import com.playdeadrespawn.storyapp.data.pref.dataStore
import com.playdeadrespawn.storyapp.data.response.ListStoryItem
import com.playdeadrespawn.storyapp.databinding.ActivityMainBinding
import com.playdeadrespawn.storyapp.view.ViewModelFactory
import com.playdeadrespawn.storyapp.view.storyadd.StoryAdd
import com.playdeadrespawn.storyapp.view.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, ViewModelFactory(UserPreference.getInstance(dataStore)))[MainViewModel::class.java]

        viewModel.getSession().observe(this) { user ->
            if (user.userId.isEmpty()) {
                startActivity(Intent(this, WelcomeActivity::class.java))
                finish()
            } else {
                viewModel.getStory(user.token)
            }
        }

        binding.rvStory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, (layoutManager as LinearLayoutManager).orientation))
            viewModel.listStory.observe(this@MainActivity) {adapter = StoryAdapter(it as ArrayList<ListStoryItem>)}
        }
        binding.fabAddStory.setOnClickListener {
            startActivity(Intent(this, StoryAdd::class.java))
        }
        setupView()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFD600")))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> viewModel.logout()
        }
        return super.onOptionsItemSelected(item)
    }
}