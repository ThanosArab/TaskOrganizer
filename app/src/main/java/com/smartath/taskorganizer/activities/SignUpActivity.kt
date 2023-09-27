package com.smartath.taskorganizer.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.smartath.taskorganizer.R
import com.smartath.taskorganizer.databinding.ActivitySignUpBinding
import com.smartath.taskorganizer.firebase.FireStoreClass
import com.smartath.taskorganizer.models.User

class SignUpActivity : BaseActivity() {

    private var binding: ActivitySignUpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        hideActionBar()
        setUpToolbar(binding?.signUpToolbar!!, resources.getString(R.string.sign_up_toolbar), R.drawable.ic_back)

        binding?.signUpBtn?.setOnClickListener {
            registerUser()
        }
    }

    fun userRegisteredSuccessfully(user: User){
        cancelProgressDialog()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun registerUser(){
        val name = binding?.nameEt?.text.toString()
        val email = binding?.emailEt?.text.toString()
        val password = binding?.passwordEt?.text.toString()

        if(validateEntryData(name, email, password)){
            showProgressDialog()
            FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    task ->
                    if (task.isSuccessful){
                        val user = task.result!!.user!!
                        val registeredEmail = user.email!!

                        val newUser = User(user.uid, name, registeredEmail)
                        FireStoreClass().registerUser(this, newUser)
                    }
                    else{
                        Toast.makeText(this, "Registration Failed!: ${task.exception!!.message}", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun validateEntryData(name: String, email: String, password: String): Boolean{
        return when{
            TextUtils.isEmpty(name) -> {
                showSnackBarError("Please enter a Name")
                false
            }
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