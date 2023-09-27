package com.smartath.taskorganizer.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.smartath.taskorganizer.R
import com.smartath.taskorganizer.databinding.ActivitySignInBinding
import com.smartath.taskorganizer.firebase.FireStoreClass
import com.smartath.taskorganizer.models.User

class SignInActivity : BaseActivity() {

    private var binding: ActivitySignInBinding? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        auth = FirebaseAuth.getInstance()

        hideActionBar()
        setUpToolbar(binding?.signInToolbar!!, resources.getString(R.string.sign_in_toolbar), R.drawable.ic_back)

        binding?.signInBtn?.setOnClickListener {
            signInRegisteredUser()
        }
    }

    fun signInSuccessfully(user: User){
        cancelProgressDialog()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun signInRegisteredUser(){
        val email = binding?.emailEt?.text.toString()
        val password = binding?.passwordEt?.text.toString()

        if (validateEntryData(email, password)){
            showProgressDialog()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    task ->
                    if (task.isSuccessful){
                        FireStoreClass().loadUserData(this)
                    }
                    else{
                        Toast.makeText(this, "Authentication Failed!: ${task.exception}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun validateEntryData(email: String, password: String): Boolean{
        return when{
            TextUtils.isEmpty(email) -> {
                showSnackBarError("Please enter an Email")
                false
            }
            TextUtils.isEmpty(password) -> {
                showSnackBarError("Please enter a Password")
                false
            }
            else -> true
        }
    }
}