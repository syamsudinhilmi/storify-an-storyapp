package com.playdeadrespawn.storyapp.view.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.playdeadrespawn.storyapp.R
import com.playdeadrespawn.storyapp.databinding.ActivityLoginBinding
import com.playdeadrespawn.storyapp.view.AuthViewModel
import com.playdeadrespawn.storyapp.view.ViewModelFactory
import com.playdeadrespawn.storyapp.view.main.MainActivity

class LoginActivity : AppCompatActivity() {
    private val viewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this)
    }
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

    }

    private fun setupAction() {
        binding.loginButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            val password = binding.passwordEditText.text.toString()

            showLoading(true)

            when {
                email.isEmpty() -> {
                    binding.emailEditTextLayout.error = getString(R.string.email_tidak_boleh_kosong)
                    showLoading(false)
                }

                password.isEmpty() -> {
                    binding.passwordEditTextLayout.error = getString(R.string.empty_password)
                    showLoading(false)
                }

                else -> {
                    viewModel.loginSession(email, password).observe(this) {
                        val data = it.loginResult
                        if (data != null && !it.error) {
                            viewModel.saveSession(data.name, data.userId, data.token)
                            showLoading(false)
                            AlertDialog.Builder(this@LoginActivity).apply {
                                setTitle(getString(R.string.selamat))
                                setMessage(getString(R.string.succes_login))
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
                        if (it.message == "400") {
                            showLoading(false)
                            AlertDialog.Builder(this@LoginActivity)
                                .setTitle(getString(R.string.error_login))
                                .setMessage(getString(R.string.error_email))
                                .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                                .show()
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

        val email =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val password =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(300)

        val sequence = AnimatorSet().apply {
            playSequentially(email, password)
        }
        AnimatorSet().apply {
            playSequentially(sequence)
            start()
        }
    }

    private fun showLoading(isLoading: Boolean) = if (isLoading) {
        binding.loadingIndicator.visibility = View.VISIBLE
    } else {
        binding.loadingIndicator.visibility = View.GONE

    }
}