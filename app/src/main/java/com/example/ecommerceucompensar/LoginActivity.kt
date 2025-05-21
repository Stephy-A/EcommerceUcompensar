package com.example.ecommerceucompensar

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ecommerceucompensar.data.AppDatabase
import com.example.ecommerceucompensar.data.UserRepository
import com.example.ecommerceucompensar.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var userRepository: UserRepository
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar el repositorio
        val database = AppDatabase.getDatabase(applicationContext)
        userRepository = UserRepository(database.userDao())

        setupButtons()
    }

    private fun setupButtons() {
        binding.btnLogin.setOnClickListener {
            if (validateInputs()) {
                loginUser()
            }
        }

        binding.btnSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        if (email.isEmpty()) {
            binding.etEmail.error = "El email es requerido"
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "La contraseña es requerida"
            return false
        }

        return true
    }

    private fun loginUser() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString()

        Log.d(TAG, "Intentando login con email: $email")

        lifecycleScope.launch {
            try {
                val user = userRepository.login(email, password)
                if (user != null) {
                    Log.d(TAG, "Login exitoso para usuario: ${user.name}")
                    Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@LoginActivity, ProductListActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Log.d(TAG, "Login fallido: credenciales inválidas")
                    Toast.makeText(this@LoginActivity, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error en login: ${e.message}")
                Toast.makeText(this@LoginActivity, "Error al iniciar sesión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
} 