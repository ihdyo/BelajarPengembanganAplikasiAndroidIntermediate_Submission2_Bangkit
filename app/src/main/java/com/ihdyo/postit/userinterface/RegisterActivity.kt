package com.ihdyo.postit.userinterface

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.ihdyo.postit.R
import com.ihdyo.postit.UserPreferences
import com.ihdyo.postit.databinding.ActivityRegisterBinding
import com.ihdyo.postit.dataclass.LoginDataAccount
import com.ihdyo.postit.dataclass.RegisterDataAccount
import com.ihdyo.postit.viewmodel.DataStoreViewModel
import com.ihdyo.postit.viewmodel.MainViewModel
import com.ihdyo.postit.viewmodel.MainViewModelFactory
import com.ihdyo.postit.viewmodel.ViewModelFactory

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    private val loginViewModel: MainViewModel by lazy {
        ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = resources.getString(R.string.createAccount)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ifClicked()

        val pref = UserPreferences.getInstance(dataStore)
        val dataStoreViewModel =
            ViewModelProvider(this, ViewModelFactory(pref))[DataStoreViewModel::class.java]
        dataStoreViewModel.getLoginSession().observe(this) { sessionTrue ->
            if (sessionTrue) {
                val intent = Intent(this@RegisterActivity, HomePageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }

        registerViewModel.message.observe(this) { messageRegist ->
            responseRegister(
                messageRegist
            )
        }

        registerViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        loginViewModel.message.observe(this) { messageLogin ->
            responseLogin(
                messageLogin,
                dataStoreViewModel
            )
        }

        loginViewModel.isLoading.observe(this) {
            showLoading(it)
        }
    }

    private fun responseLogin(
        message: String,
        dataStoreViewModel: DataStoreViewModel
    ) {
        if (message.contains("Hello")) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            val user = loginViewModel.userlogin.value
            dataStoreViewModel.saveLoginSession(true)
            dataStoreViewModel.saveToken(user?.loginResult!!.token)
            dataStoreViewModel.saveName(user.loginResult.name)
        } else {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }

    private fun responseRegister(
        message: String,
    ) {
        if (message == "Account created") {
            val userLogin = LoginDataAccount(binding.RegistEmail.text.toString(), binding.RegistPassword.text.toString())
            loginViewModel.login(userLogin)
            val intent = Intent(this@RegisterActivity, HomePageActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        } else {
            if (message.contains("Email was taken, try another")) {
                binding.RegistEmail.setErrorMessage(
                    resources.getString(R.string.emailTaken),
                    binding.RegistEmail.text.toString()
                )
                Toast.makeText(this, resources.getString(R.string.emailTaken), Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun ifClicked() {
        binding.seeRegistPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.RegistPassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
                binding.RetypePassword.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                binding.RegistPassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
                binding.RetypePassword.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }

            // Set selection to end of text
            binding.RegistPassword.text?.let { binding.RegistPassword.setSelection(it.length) }
            binding.RetypePassword.text?.let { binding.RetypePassword.setSelection(it.length) }
        }

        binding.btnRegistAccount.setOnClickListener {
            binding.apply {
                RegistName.clearFocus()
                RegistEmail.clearFocus()
                RegistPassword.clearFocus()
                RetypePassword.clearFocus()
            }

            if (binding.RegistName.isNameValid && binding.RegistEmail.isEmailValid && binding.RegistPassword.isPasswordValid && binding.RetypePassword.isPasswordValid) {
                val dataRegisterAccount = RegisterDataAccount(
                    name = binding.RegistName.text.toString().trim(),
                    email = binding.RegistEmail.text.toString().trim(),
                    password = binding.RegistPassword.text.toString().trim()
                )

                registerViewModel.register(dataRegisterAccount)
            } else {
                if (!binding.RegistName.isNameValid) binding.RegistName.error =
                    resources.getString(R.string.nameNone)
                if (!binding.RegistEmail.isEmailValid) binding.RegistEmail.error =
                    resources.getString(R.string.emailNone)
                if (!binding.RegistPassword.isPasswordValid) binding.RegistPassword.error =
                    resources.getString(R.string.passwordNone)
                if (!binding.RetypePassword.isPasswordValid) binding.RetypePassword.error =
                    resources.getString(R.string.passwordConfirmNone)

                Toast.makeText(this, R.string.invalidLogin, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar2.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        finish()
        return super.onSupportNavigateUp()
    }
}