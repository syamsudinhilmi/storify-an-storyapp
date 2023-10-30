package com.playdeadrespawn.storyapp.view.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.playdeadrespawn.storyapp.data.pref.UserPreference
import com.playdeadrespawn.storyapp.data.pref.dataStore
import com.playdeadrespawn.storyapp.databinding.ActivitySignupBinding
import com.playdeadrespawn.storyapp.view.AuthViewModel
import com.playdeadrespawn.storyapp.view.ViewModelFactory

class SignupActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel
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

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[AuthViewModel::class.java]
    }

    private fun setupAction() {
        binding.signupButton.setOnClickListener {
            val name = binding.nameEditText.text.toString()
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            showLoading(true)
            viewModel.registerSession(name, email, password)
        }
        viewModel.register.observe(this) {
            if (it) {
                showLoading(false)
                val email = binding.emailEditText.text.toString()
                AlertDialog.Builder(this@SignupActivity).apply {
                    setTitle("Selamat!")
                    setMessage("Akun dengan $email sudah jadi nih. Silahkan lanjut Login.")
                    setPositiveButton("Lanjut") { _, _ ->
                        finish()
                    }
                    create()
                    show()
                }
            } else {
                showLoading(false)
                androidx.appcompat.app.AlertDialog.Builder(this@SignupActivity)
                    .setTitle("Signup Gagal")
                    .setMessage("Email atau password tidak sesuai. Coba ulangi lagi!.")
                    .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    .show()
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