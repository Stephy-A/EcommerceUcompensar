package com.example.ecommerceucompensar

import com.example.ecommerceucompensar.data.Product

object CartManager {
    private val cartItems = mutableListOf<CartItem>()
    private var totalPrice = 0.0

    fun getCartItems(): List<CartItem> = cartItems.toList()

    fun getTotalPrice(): Double = totalPrice

    fun addProduct(product: Product) {
        val existingItem = cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems.add(CartItem(product))
        }
        totalPrice += product.price
    }

    fun removeProduct(product: Product) {
        val existingItem = cartItems.find { it.product.id == product.id }
        existingItem?.let {
            if (it.quantity > 1) {
                it.quantity--
            } else {
                cartItems.remove(it)
            }
            totalPrice -= product.price
        }
    }

    fun clearCart() {
        cartItems.clear()
        totalPrice = 0.0
    }
} 