package com.example.ecommerceucompensar

import com.example.ecommerceucompensar.data.Product

data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    fun getTotalPrice(): Double = product.price * quantity
} 