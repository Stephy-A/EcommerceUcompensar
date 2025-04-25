package com.example.ecommerceucompensar

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val imageResId: Int = android.R.drawable.ic_menu_gallery  // Valor por defecto
) : Parcelable 