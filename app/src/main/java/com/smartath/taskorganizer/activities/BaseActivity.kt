package com.smartath.taskorganizer.activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.smartath.taskorganizer.R
import java.io.IOException

open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitOnce: Boolean = false

    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    private lateinit var progressDialog: Dialog

    val storagePermission: ActivityResultLauncher<Array<String>> = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
        permissions ->
        permissions.entries.forEach {
            val permission = it.key
            val isGranted = it.value

            if(isGranted){
                imageChooser()
            }
            else{
                if (permission == Manifest.permission.READ_EXTERNAL_STORAGE){
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun showProgressDialog(){
        progressDialog = Dialog(this)
        progressDialog.setContentView(R.layout.dialog_progress_bar)
        progressDialog.show()
    }

    fun cancelProgressDialog(){
        progressDialog.dismiss()
    }

    fun getCurrentUserId(): String{
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun hideActionBar(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }
        else{
            @Suppress("Deprecation")
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    fun setUpToolbar(toolbar: Toolbar, title: String, drawable: Int){
        setSupportActionBar(toolbar)
        if(supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = title
            supportActionBar?.setHomeAsUpIndicator(drawable)

            toolbar.setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    fun showSnackBarError(message: String){
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(ContextCompat.getColor(this, R.color.snack_bar_color))
        snackBar.show()
    }

    fun doubleBackToExit(){
        if(doubleBackToExitOnce){
            onBackPressedDispatcher.onBackPressed()
            return
        }
        this.doubleBackToExitOnce = true

        Toast.makeText(this, "Please click again to exit from application", Toast.LENGTH_LONG).show()

        Handler(Looper.getMainLooper()).postDelayed({ doubleBackToExitOnce = false }, 2000)
    }

    fun imageWithGlide(activity: Activity, imageView: ImageView, uri: Uri?){
        imageView.let {
            Glide
                .with(activity)
                .load(uri)
                .centerCrop()
                .placeholder(R.drawable.ic_user)
                .into(it)
        }

    }

    protected fun launchGalleryIntent(function: (imageUri: Uri?) -> Unit){
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if(result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if(data != null) {
                    try {
                        // The uri of selection image from phone storage.
                        val fileUri: Uri? = data.data
                        Log.e("Saved image: ", "Path :: $fileUri")

                        // Load the user image in the ImageView.
                        function(fileUri)

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(this, "Failed to load the Image from Gallery", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun imageChooser(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryLauncher.launch(intent)
    }

    fun showDialogToSetPermissionFromSettings(activity: Activity){
        AlertDialog.Builder(activity).setMessage("It looks like you have turned off the permission required for this " +
                "feature. It can be enabled in 'Application Settings'")
            .setPositiveButton("Go to settings"){_,_->
                try {//takes the user to the application settings

                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package",activity.packageName,null)
                    intent.data = uri
                    activity.startActivity(intent)
                }
                catch (e: ActivityNotFoundException){
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel"){dialog,_->
                dialog.dismiss()
            }.show()
    }

    fun getFileExtension(uri: Uri?, activity: Activity): String?{
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }



}