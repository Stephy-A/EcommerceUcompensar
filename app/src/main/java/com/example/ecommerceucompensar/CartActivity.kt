package com.example.ecommerceucompensar

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ecommerceucompensar.data.Product
import com.example.ecommerceucompensar.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private val TAG = "CartActivity"
    private var currentProduct: Product? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            // Obtener los datos del producto del intent
            val productId = intent.getIntExtra("product_id", -1)
            val productName = intent.getStringExtra("product_name") ?: ""
            val productDescription = intent.getStringExtra("product_description") ?: ""
            val productPrice = intent.getDoubleExtra("product_price", 0.0)
            val productImage = intent.getIntExtra("product_image", 0)

            // Crear el objeto Product
            currentProduct = Product(
                id = productId,
                name = productName,
                description = productDescription,
                price = productPrice,
                imageResId = productImage
            )

            // Mostrar los datos del producto
            binding.apply {
                tvProductName.text = productName
                tvProductDescription.text = productDescription
                tvProductPrice.text = "$ ${String.format("%.2f", productPrice)}"
                ivProduct.setImageResource(productImage)
            }

            setupRecyclerView()
            setupButtons()
            updateTotalPrice()

        } catch (e: Exception) {
            Log.e(TAG, "Error al procesar los datos del producto: ${e.message}")
            Toast.makeText(this, "Error al cargar el producto", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun setupButtons() {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnAddToCart.setOnClickListener {
            currentProduct?.let { product ->
                CartManager.addProduct(product)
                cartAdapter.updateItems(CartManager.getCartItems())
                updateTotalPrice()
                Toast.makeText(this, "Producto agregado al carrito", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = CartAdapter(CartManager.getCartItems()) { product ->
            CartManager.removeProduct(product)
            cartAdapter.updateItems(CartManager.getCartItems())
            updateTotalPrice()
        }

        binding.rvCartItems.apply {
            layoutManager = LinearLayoutManager(this@CartActivity)
            adapter = cartAdapter
        }
    }

    private fun updateTotalPrice() {
        binding.tvTotalPrice.text = "$ ${String.format("%.2f", CartManager.getTotalPrice())}"
    }
} 