package com.example.ecommerceucompensar

import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ecommerceucompensar.data.AppDatabase
import com.example.ecommerceucompensar.data.User
import com.example.ecommerceucompensar.data.UserRepository
import com.example.ecommerceucompensar.databinding.ActivitySignUpBinding
import kotlinx.coroutines.launch

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var userRepository: UserRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el repositorio
        val database = AppDatabase.getDatabase(applicationContext)
        userRepository = UserRepository(database.userDao())

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnRegister.setOnClickListener {
            if (validateInputs()) {
                registerUser()
            }
        }

        binding.btnBackToLogin.setOnClickListener {
            finish()
        }
    }

    private fun validateInputs(): Boolean {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        val phone = binding.etPhone.text.toString().trim()

        if (name.isEmpty()) {
            binding.etName.error = "El nombre es requerido"
            return false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "El email es requerido"
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Email inválido"
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "La contraseña es requerida"
            return false
        }

        if (password.length < 6) {
            binding.etPassword.error = "La contraseña debe tener al menos 6 caracteres"
            return false
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Las contraseñas no coinciden"
            return false
        }

        if (phone.isEmpty()) {
            binding.etPhone.error = "El teléfono es requerido"
            return false
        }

        return true
    }

    private fun registerUser() {
        val user = User(
            name = binding.etName.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            password = binding.etPassword.text.toString(),
            phone = binding.etPhone.text.toString().trim()
        )

        lifecycleScope.launch {
            try {
                userRepository.register(user).fold(
                    onSuccess = {
                        Toast.makeText(this@SignUpActivity, "Registro exitoso", Toast.LENGTH_SHORT).show()
                        finish()
                    },
                    onFailure = { exception ->
                        Toast.makeText(this@SignUpActivity, exception.message, Toast.LENGTH_SHORT).show()
                    }
                )
            } catch (e: Exception) {
                Toast.makeText(this@SignUpActivity, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 