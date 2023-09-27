package com.smartath.taskorganizer.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.smartath.taskorganizer.R
import com.smartath.taskorganizer.databinding.ActivitySplashBinding
import com.smartath.taskorganizer.firebase.FireStoreClass

class SplashActivity : BaseActivity() {

    private var binding: ActivitySplashBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        hideActionBar()

        animationIntent()

        binding?.container?.setTransition(R.id.trans)
        binding?.container?.animate()
    }

    private fun animationIntent(){

        Handler(Looper.getMainLooper()).postDelayed({

            val currentUserId = FireStoreClass().getCurrentUserId()

            val options = ActivityOptions.makeSceneTransitionAnimation(this)

            if (currentUserId.isNotEmpty()){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent, options.toBundle())
            }
            else{
                val intent = Intent(this, IntroActivity::class.java)
                startActivity(intent, options.toBundle())
            }

            finish()
        }, 2500)
    }
}