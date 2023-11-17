package com.playdeadrespawn.storyapp.view.storyadd

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.playdeadrespawn.storyapp.R
import com.playdeadrespawn.storyapp.databinding.ActivityStoryAddBinding
import com.playdeadrespawn.storyapp.utils.reduceFileImage
import com.playdeadrespawn.storyapp.utils.rotateBitmap
import com.playdeadrespawn.storyapp.utils.uriToFile
import com.playdeadrespawn.storyapp.view.ViewModelFactory
import com.playdeadrespawn.storyapp.view.main.MainActivity
import java.io.File

class StoryAdd : AppCompatActivity() {
    private val viewModel by viewModels<StoryAddViewModel>() {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityStoryAddBinding
    private var lon = 0.0
    private var lat = 0.0

    private var getFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.add_story)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            galleryButton.setOnClickListener { openGallery() }
            cameraButton.setOnClickListener { openCamera() }
            uploadButton.setOnClickListener {
                getFile?.let { file ->
                    val description = binding.descEditText.text.toString()
                    if (description.isNotEmpty()) {
                        viewModel.getSession().observe(this@StoryAdd) { user ->
                            viewModel.newStory(user.token, reduceFileImage(file), description, lon.toString(), lat.toString())
                            AlertDialog.Builder(this@StoryAdd).apply {
                                setMessage(getString(R.string.story_berhasil_ditambahkan))
                                setPositiveButton("Ok") { _, _ ->
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                    startActivity(intent)
                                    finish()
                                }
                                create()
                                show()
                            }
                        }
                    } else {
                        showToast(getString(R.string.deskripsi_tidak_boleh_kosong))
                    }
                } ?: showToast(getString(R.string.gambar_tidak_boleh_kosong))
            }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS && !allPermissionsGranted()) {
            showToast(getString(R.string.tidak_mendapatkan_permission))
            finish()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    companion object {
        const val CAMERA_X_RESULT = 200
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}