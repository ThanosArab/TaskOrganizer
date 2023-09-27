package com.smartath.taskorganizer.activities

import android.content.Intent
import android.os.Bundle
import com.smartath.taskorganizer.databinding.ActivityIntroBinding

class IntroActivity : BaseActivity() {

    private var binding: ActivityIntroBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityIntroBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        hideActionBar()

        binding?.signInBtn?.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
        }
        binding?.signUpBtn?.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
    }
}