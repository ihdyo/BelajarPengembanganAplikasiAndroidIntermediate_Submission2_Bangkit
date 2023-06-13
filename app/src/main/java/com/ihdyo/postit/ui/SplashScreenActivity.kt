package com.ihdyo.postit.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ihdyo.postit.R
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
        val loginViewModel = ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]

        loginViewModel.getLoginSession().observe(this) { isLoggedIn ->
            Handler().postDelayed({
                val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = connectivityManager.activeNetworkInfo

                if (activeNetwork?.isConnected == true) {
                    val intent = if (isLoggedIn) {
                        Intent(this, HomePageActivity::class.java)
                    } else {
                        Intent(this, LoginActivity::class.java)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setTitle("No Connection")
                    builder.setMessage("Check your internet connection")
                    builder.setPositiveButton("Connect") { _, _ ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        this.startActivity(settingsIntent)
                    }
                    builder.setNegativeButton("Cancel") { _, _ -> }
                    builder.show()
                }
            }, 1000)
        }
    }
}