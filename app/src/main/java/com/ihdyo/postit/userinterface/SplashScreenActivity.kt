package com.ihdyo.postit.userinterface

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ihdyo.postit.UserPreferences
import com.ihdyo.postit.databinding.ActivitySplashScreenBinding
import com.ihdyo.postit.viewmodel.DataStoreViewModel
import com.ihdyo.postit.viewmodel.ViewModelFactory

@SuppressLint("CustomSplashScreen")
class SplashScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreferences.getInstance(dataStore)
        val loginViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        loginViewModel.getLoginSession().observe(this) { isLoggedIn ->
            val splashScreenTextBottom =
                ObjectAnimator.ofFloat(binding.bottomSplashScreen, View.ALPHA, 1f).setDuration(2000)

            AnimatorSet().apply {
                playTogether(splashScreenTextBottom)
                start()
            }

            val intent = if (isLoggedIn) {
                Intent(this, HomePageActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }

            binding.icon.animate()
                .setDuration(3000)
                .alpha(0f)
                .withEndAction {
                    startActivity(intent)
                    finish()
                }
        }
    }
}