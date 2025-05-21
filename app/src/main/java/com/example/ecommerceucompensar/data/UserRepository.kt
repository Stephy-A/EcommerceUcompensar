package com.example.ecommerceucompensar.data

import android.util.Log
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {
    private val TAG = "UserRepository"
    val allUsers: Flow<List<User>> = userDao.getAllUsers()

    suspend fun login(email: String, password: String): User? {
        return try {
            Log.d(TAG, "Intentando login para email: $email")
            val user = userDao.login(email, password)
            if (user != null) {
                Log.d(TAG, "Login exitoso para usuario: ${user.name}")
            } else {
                Log.d(TAG, "Login fallido: credenciales inválidas")
            }
            user
        } catch (e: Exception) {
            Log.e(TAG, "Error en login: ${e.message}")
            null
        }
    }

    suspend fun register(user: User): Result<Unit> {
        return try {
            Log.d(TAG, "Intentando registrar usuario con email: ${user.email}")
            // Verificar si el email ya está registrado
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                Log.d(TAG, "Registro fallido: email ya registrado")
                Result.failure(Exception("El email ya está registrado"))
            } else {
                userDao.insertUser(user)
                Log.d(TAG, "Usuario registrado exitosamente")
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error en registro: ${e.message}")
            Result.failure(Exception("Error al registrar usuario: ${e.message}"))
        }
    }

    suspend fun updateUser(user: User) {
        try {
            userDao.updateUser(user)
            Log.d(TAG, "Usuario actualizado exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar usuario: ${e.message}")
            throw Exception("Error al actualizar usuario: ${e.message}")
        }
    }

    suspend fun deleteUser(user: User) {
        try {
            userDao.deleteUser(user)
            Log.d(TAG, "Usuario eliminado exitosamente")
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar usuario: ${e.message}")
            throw Exception("Error al eliminar usuario: ${e.message}")
        }
    }

    suspend fun getUserByEmail(email: String): User? {
        return try {
            userDao.getUserByEmail(email)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener usuario por email: ${e.message}")
            null
        }
    }
} 