package com.smartath.taskorganizer.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.smartath.taskorganizer.R
import com.smartath.taskorganizer.databinding.ActivityProfileBinding
import com.smartath.taskorganizer.firebase.FireStoreClass
import com.smartath.taskorganizer.models.User
import com.smartath.taskorganizer.utils.Constants
import java.io.IOException

class ProfileActivity : BaseActivity() {

    private var binding: ActivityProfileBinding? = null

    private lateinit var userDetails: User

    private var selectedImageUri: Uri? = null

    private var profileImageUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setUpToolbar(binding?.profileToolbar!!, "Profile", R.drawable.ic_back_white)

        FireStoreClass().loadUserData(this)

        binding?.profileImageIv?.setOnClickListener {
            requestStorageAccess()
        }

        binding?.updateBtn?.setOnClickListener {
            if (selectedImageUri != null){
                uploadProfileImageToDatabase()
            }
            else{
                showProgressDialog()
                updateProfileData()
            }
        }

        launchGalleryIntent(function =  { imageUri -> updateProfileImage(imageUri) })
    }

    fun setUpUserUiData(user: User){
        userDetails = user

        imageWithGlide(this@ProfileActivity, binding?.profileImageIv!!, user.image.toUri())
        binding?.profileNameEt?.setText(user.name)
        binding?.profileEmailEt?.setText(user.email)
        if (user.mobile != 0L){
            binding?.profileMobileEt?.setText(user.mobile.toString())
        }
    }

    private fun requestStorageAccess(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            if (Environment.isExternalStorageManager()){
                imageChooser()
            }
            else{
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
        }
        else{
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
                showDialogToSetPermissionFromSettings(this)
            }
            else{
                storagePermission.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            }
        }
    }

    private fun uploadProfileImageToDatabase(){
        showProgressDialog()

        if(selectedImageUri != null){
            val storage: StorageReference = FirebaseStorage.getInstance().reference
                .child("USER_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(selectedImageUri, this@ProfileActivity))

            storage.putFile(selectedImageUri!!).addOnSuccessListener {
                taskSnapShot ->

                Log.e("Firebase Image URL", taskSnapShot.metadata!!.reference!!.downloadUrl.toString())

                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    profileImageUrl = uri.toString()

                    updateProfileData()
                }
            }.addOnFailureListener{
                e ->
                cancelProgressDialog()
                Log.e(this.javaClass.simpleName, "Error: $e")
            }
        }
    }

    fun profileUpdatedSuccessfully(){
        cancelProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun updateProfileData(){
        val userHashMap = HashMap<String, Any>()

        var anyChangesMade = false

        if (profileImageUrl.isNotEmpty() && profileImageUrl != userDetails.image){
            userHashMap[Constants.IMAGE] = profileImageUrl
            anyChangesMade = true
        }
        if(binding?.profileNameEt?.text.toString() != userDetails.name){
            userHashMap[Constants.NAME] = binding?.profileNameEt?.text.toString()
            anyChangesMade = true
        }
        if(binding?.profileMobileEt?.text.toString() != userDetails.mobile.toString()){
            userHashMap[Constants.MOBILE] = binding?.profileMobileEt?.text.toString().toLong()
            anyChangesMade = true
        }

        if(anyChangesMade){
            FireStoreClass().updateUserData(this@ProfileActivity, userHashMap)
        }
        else{
            cancelProgressDialog()
            Toast.makeText(this@ProfileActivity, "No changes have been made!", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateProfileImage(uri: Uri?) {
        selectedImageUri = uri
        try{
            Glide
                .with(this@ProfileActivity)
                .load(selectedImageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user)
                .into(binding?.profileImageIv!!)

        }
        catch (e: IOException){
            e.printStackTrace()
        }
    }
}