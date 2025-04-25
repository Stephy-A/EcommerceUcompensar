package com.example.ecommerceucompensar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.ecommerceucompensar.databinding.ItemCartBinding

class CartAdapter(
    private val cartItems: MutableList<CartItem>,
    private val onQuantityChanged: (Double) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(
        private val binding: ItemCartBinding,
        private val onQuantityChanged: (Double) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(cartItem: CartItem) {
            binding.ivProductImage.setImageResource(cartItem.product.imageResId)
            binding.tvProductName.text = cartItem.product.name
            binding.tvProductPrice.text = "$${cartItem.product.price}"
            binding.tvQuantity.text = cartItem.quantity.toString()

            binding.btnIncrease.setOnClickListener {
                cartItem.quantity++
                binding.tvQuantity.text = cartItem.quantity.toString()
                onQuantityChanged(cartItem.getTotalPrice())
            }

            binding.btnDecrease.setOnClickListener {
                if (cartItem.quantity > 1) {
                    cartItem.quantity--
                    binding.tvQuantity.text = cartItem.quantity.toString()
                    onQuantityChanged(-cartItem.product.price)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CartViewHolder(binding, onQuantityChanged)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount() = cartItems.size

    fun removeItem(position: Int) {
        val removedItem = cartItems.removeAt(position)
        onQuantityChanged(-removedItem.getTotalPrice())
        notifyItemRemoved(position)
    }
} 