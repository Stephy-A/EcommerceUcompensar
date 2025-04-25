package com.example.ecommerceucompensar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceucompensar.databinding.ItemProductBinding

class ProductAdapter(
    private val products: List<Product>,
    private val onAddToCart: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(
        private val binding: ItemProductBinding,
        private val onAddToCart: (Product) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(product: Product) {
            binding.tvProductName.text = product.name
            binding.tvProductDescription.text = product.description
            binding.tvProductPrice.text = "$${product.price}"
            
            // Usar la imagen específica del producto
            binding.ivProduct.setImageResource(product.imageResId)

            binding.root.setOnClickListener {
                onAddToCart(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProductViewHolder(binding, onAddToCart)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size
} 