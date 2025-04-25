package com.example.ecommerceucompensar

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceucompensar.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val cartItems = mutableListOf<CartItem>()
    private var totalPrice = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        updateTotalPrice()

        // Obtener el producto del intent si existe
        val product = intent.getParcelableExtra<Product>("product")
        if (product != null) {
            addProductToCart(product)
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(cartItems) { priceChange ->
            totalPrice += priceChange
            updateTotalPrice()
        }

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }
    }

    private fun updateTotalPrice() {
        binding.tvTotalPrice.text = "$${String.format("%.2f", totalPrice)}"
    }

    private fun addProductToCart(product: Product) {
        val existingItem = cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity++
            totalPrice += product.price
        } else {
            cartItems.add(CartItem(product))
            totalPrice += product.price
        }
        cartAdapter.notifyDataSetChanged()
        updateTotalPrice()
    }
} 