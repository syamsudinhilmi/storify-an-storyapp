package com.playdeadrespawn.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.playdeadrespawn.storyapp.data.pref.UserModel
import com.playdeadrespawn.storyapp.data.pref.UserPreference
import com.playdeadrespawn.storyapp.data.pref.dataStore
import com.playdeadrespawn.storyapp.databinding.ActivityLoginBinding
import com.playdeadrespawn.storyapp.view.AuthViewModel
import com.playdeadrespawn.storyapp.view.ViewModelFactory
import com.playdeadrespawn.storyapp.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
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

        viewModel.let {
            it.login.observe(this) {login->
                it.saveSession(
                    UserModel(
                        login.loginResult.name,
                        login.loginResult.token,
                        login.loginResult.userId)
                )
            }
        }
    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            showLoading(true)

            when {
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = "Email tidak boleh kosong"
                    showLoading(false)
                }
                else -> {
                    viewModel.loginSession(email, password)
                    viewModel.loading.observe(this) {
                        showLoading(it)
                        if (!it) {
                            AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle("Login Gagal")
                                setMessage("Email atau password tidak sesuai. Coba ulangi lagi!.")
                                setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                                show()
                            }
                        }
                    }
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

        val email = ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val password = ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(300)

        val sequence = AnimatorSet().apply {
            playSequentially(email, password)
        }
        AnimatorSet().apply {
            playSequentially(sequence)
            start()
        }
    }
    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.loadingIndicator.visibility = View.VISIBLE
        } else {
            binding.loadingIndicator.visibility = View.GONE

            viewModel.login.observe(this) {
                if (!it.error) {
                    AlertDialog.Builder(this@LoginActivity).apply {
                        setTitle("Selamat!")
                        setMessage("Anda berhasil login. Silahkan menggunakan aplikasi ini.")
                        setPositiveButton("Lanjut") { _, _ ->
                            val intent = Intent(context, MainActivity::class.java)
                            intent.flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                            finish()
                        }
                        create()
                        show()
                    }
                }
            }
        }
    }
}