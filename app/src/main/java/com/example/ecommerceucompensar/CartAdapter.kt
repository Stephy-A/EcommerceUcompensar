package com.example.ecommerceucompensar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceucompensar.data.Product
import com.example.ecommerceucompensar.databinding.ItemCartBinding

class CartAdapter(
    private var items: List<CartItem>,
    private val onRemoveItem: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class CartViewHolder(
        private val binding: ItemCartBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            binding.apply {
                tvProductName.text = cartItem.product.name
                tvQuantity.text = "Cantidad: ${cartItem.quantity}"
                tvProductPrice.text = "$ ${String.format("%.2f", cartItem.product.price * cartItem.quantity)}"
                
                btnRemove.setOnClickListener {
                    onRemoveItem(cartItem.product)
                }
            }
        }
    }
} 