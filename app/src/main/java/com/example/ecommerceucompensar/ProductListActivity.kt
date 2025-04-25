package com.example.ecommerceucompensar

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceucompensar.databinding.ActivityProductListBinding

class ProductListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductListBinding
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupCartButton()
    }

    private fun setupRecyclerView() {
        val products = listOf(
            Product(
                id = 1,
                name = "Smartphone",
                description = "Último modelo con cámara de alta resolución",
                price = 999.99,
                imageResId = R.drawable.smartphone
            ),
            Product(
                id = 2,
                name = "Laptop",
                description = "Procesador de última generación",
                price = 1299.99,
                imageResId = R.drawable.laptop
            ),
            Product(
                id = 3,
                name = "Smart TV",
                description = "4K HDR con Android TV",
                price = 799.99,
                imageResId = R.drawable.smartv
            ),
            Product(
                id = 4,
                name = "Auriculares Inalámbricos",
                description = "Cancelación de ruido activa",
                price = 199.99,
                imageResId = R.drawable.auriculares
            ),
            Product(
                id = 5,
                name = "Smartwatch",
                description = "Monitoreo de salud y fitness",
                price = 299.99,
                imageResId = R.drawable.smartwatch
            )
        )

        adapter = ProductAdapter(products) { product ->
            val intent = Intent(this, CartActivity::class.java)
            intent.putExtra("product", product)
            startActivity(intent)
        }

        binding.rvProducts.apply {
            layoutManager = LinearLayoutManager(this@ProductListActivity)
            adapter = this@ProductListActivity.adapter
        }
    }

    private fun setupCartButton() {
        binding.btnCart.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }
}