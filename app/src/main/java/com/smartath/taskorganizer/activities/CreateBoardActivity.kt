package com.smartath.taskorganizer.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.smartath.taskorganizer.R
import com.smartath.taskorganizer.databinding.ActivityCreateBoardBinding
import com.smartath.taskorganizer.firebase.FireStoreClass
import com.smartath.taskorganizer.models.Board
import com.smartath.taskorganizer.utils.Constants
import java.io.IOException

class CreateBoardActivity : BaseActivity() {

    private var binding: ActivityCreateBoardBinding? = null

    private var selectedImageUri: Uri? = null

    private var boardImageUrl: String = ""

    private var userName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBoardBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setUpToolbar(binding?.boardToolbar!!, "Create Board", R.drawable.ic_back_white)

        if(intent.hasExtra(Constants.NAME)){
            userName = intent.getStringExtra(Constants.NAME)!!
        }

        binding?.boardImageIv?.setOnClickListener {
            requestStorageAccess()
        }

        binding?.createBoardBtn?.setOnClickListener {
            if(selectedImageUri != null){
                uploadBoardImageToDatabase()
            }
            else{
                showProgressDialog()
                createNewBoard()
            }
        }

        launchGalleryIntent(function =  { imageUri ->  updateBoardImage(imageUri) })
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

    fun boardCreatedSuccessfully(){
        cancelProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun createNewBoard(){
        val assignedToUsers : ArrayList<String> = ArrayList()
        assignedToUsers.add(getCurrentUserId())

        var board = Board(binding?.boardNameEt?.text.toString(), boardImageUrl, userName, assignedToUsers)
        FireStoreClass().createBoard(this, board)
    }

    private fun uploadBoardImageToDatabase(){
        showProgressDialog()

        if (selectedImageUri != null){
            val storage : StorageReference = FirebaseStorage.getInstance().reference
                .child("BOARD_IMAGE" + System.currentTimeMillis() + "." + getFileExtension(selectedImageUri, this))

            storage.putFile(selectedImageUri!!).addOnSuccessListener {
                taskSnapShot ->
                taskSnapShot.metadata!!.reference!!.downloadUrl.addOnSuccessListener {
                    uri ->
                    boardImageUrl = uri.toString()

                    createNewBoard()
                }
            }.addOnFailureListener{
                e ->
                cancelProgressDialog()
                e.printStackTrace()
            }
        }
    }

    private fun updateBoardImage(uri: Uri?) {
        selectedImageUri = uri
        try{
            Glide
                .with(this@CreateBoardActivity)
                .load(selectedImageUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user)
                .into(binding?.boardImageIv!!)

        }
        catch (e: IOException){
            e.printStackTrace()
        }
    }
}