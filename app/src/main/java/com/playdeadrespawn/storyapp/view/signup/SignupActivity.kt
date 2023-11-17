package com.playdeadrespawn.storyapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.playdeadrespawn.storyapp.R
import com.playdeadrespawn.storyapp.data.pref.UserPreference
import com.playdeadrespawn.storyapp.data.pref.dataStore
import com.playdeadrespawn.storyapp.databinding.ActivitySignupBinding
import com.playdeadrespawn.storyapp.view.AuthViewModel
import com.playdeadrespawn.storyapp.view.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupView()
        setupAction()
        playAnimation()
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
        supportActionBar?.hide()

    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            showLoading(true)
            viewModel.registerSession(name, email, password).observe(this){
                if (it.message == "201") {
                    showLoading(false)
                    AlertDialog.Builder(this@SignupActivity).apply {
                        setTitle(getString(R.string.selamat))
                        setMessage(
                            getString(
                                R.string.succes_signup,
                                email
                            ))
                        setPositiveButton(getString(R.string.lanjut)) { _, _ ->
                            finish()
                        }
                        create()
                        show()
                    }
                } else if(it.message == "400") {
                    showLoading(false)
                    androidx.appcompat.app.AlertDialog.Builder(this@SignupActivity)
                        .setTitle(getString(R.string.signup_gagal))
                        .setMessage(getString(R.string.error_signup))
                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                        .show()
                }
            }
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val name = ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val email = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val password = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(300)

        val sequence = AnimatorSet().apply {
            playSequentially(name, email, password)
        }
        AnimatorSet().apply {
            playSequentially(sequence)
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.loadingIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}