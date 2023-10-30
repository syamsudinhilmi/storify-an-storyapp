package com.playdeadrespawn.storyapp.view.storyadd

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.playdeadrespawn.storyapp.data.pref.UserPreference
import com.playdeadrespawn.storyapp.data.pref.dataStore
import com.playdeadrespawn.storyapp.databinding.ActivityStoryAddBinding
import com.playdeadrespawn.storyapp.utils.reduceFileImage
import com.playdeadrespawn.storyapp.utils.rotateBitmap
import com.playdeadrespawn.storyapp.utils.uriToFile
import com.playdeadrespawn.storyapp.view.ViewModelFactory
import com.playdeadrespawn.storyapp.view.main.MainActivity
import java.io.File

class StoryAdd : AppCompatActivity() {
    private lateinit var binding: ActivityStoryAddBinding
    private lateinit var viewModel: StoryAddViewModel
    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(UserPreference.getInstance(dataStore))
        )[StoryAddViewModel::class.java]

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS)
        }

        binding.apply {
            galleryButton.setOnClickListener { openGallery() }
            cameraButton.setOnClickListener { openCamera() }
            uploadButton.setOnClickListener { handleUploadClick() }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply { type = "image/*" }
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        intentGallery.launch(chooser)
    }

    private val intentGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                result.data?.data?.let { image ->
                    getFile = uriToFile(image, this)
                    binding.previewImageView.setImageURI(image)
                }
            }
        }

    private fun openCamera() {
        val intent = Intent(this, CameraActivity::class.java)
        intentCamera.launch(intent)
    }

    private val intentCamera =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == CAMERA_X_RESULT) {
                val file = result.data?.getSerializableExtra("picture") as? File
                val isBackCamera = result.data?.getBooleanExtra("isBackCamera", true) ?: true

                file?.let { getFile = it }
                getFile?.let {
                    val resultBitmap = rotateBitmap(BitmapFactory.decodeFile(it.path), isBackCamera)
                    binding.previewImageView.setImageBitmap(resultBitmap)
                }
            }
        }

    private fun handleUploadClick() {
        getFile?.let { file ->
            val description = binding.descEditText.text.toString()
            if (description.isNotEmpty()) {
                viewModel.getSession().observe(this@StoryAdd) { user ->
                    viewModel.newStory(user.token, reduceFileImage(file), description)
                    showToast("Story berhasil ditambahkan")
                    val intent = Intent(this@StoryAdd, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
            } else {
                showToast("Deskripsi tidak boleh kosong")
            }
        } ?: showToast("Gambar tidak boleh kosong")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && !allPermissionsGranted()) {
            showToast("Tidak mendapatkan permission.")
            finish()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}