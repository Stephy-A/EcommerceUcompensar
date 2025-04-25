package com.example.ecommerceucompensar

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.ecommerceucompensar.databinding.ActivitySignupBinding

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupContinueButton()
    }

    private fun setupContinueButton() {
        binding.btnContinue.setOnClickListener{
            val name = binding.etName.text.toString()
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (username.isEmpty() || password.isEmpty() || name.isEmpty() || email.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // TODO: Implement Register logic here
            Toast.makeText(this, "User successfully registered", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity
            ::class.java)
            startActivity(intent)
        }
    }
} 