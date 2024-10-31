package com.maruf.devstream.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.maruf.devstream.R
import com.maruf.devstream.model.Product
import com.maruf.devstream.databinding.ItemProductBinding

class ProductAdapter(private var products: List<Product> = listOf()) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var isLoading = true

    companion object {
        private const val PRODUCT_VIEW_TYPE = 1
        private const val SHIMMER_VIEW_TYPE = 2
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.product = product
            binding.executePendingBindings()
        }
    }

    inner class ShimmerViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SHIMMER_VIEW_TYPE) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.shimmer_item_product, parent, false)
            ShimmerViewHolder(view)
        } else {
            val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ProductViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductViewHolder) {
            holder.bind(products[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (isLoading && position >= products.size) SHIMMER_VIEW_TYPE else PRODUCT_VIEW_TYPE
    }

    override fun getItemCount(): Int {
        return if (isLoading) products.size + 5 else products.size
    }

    // Toggle loading state
    fun setLoading(isLoading: Boolean) {
        this.isLoading = isLoading
        notifyDataSetChanged()
    }

    // Update product list and disable loading
    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        setLoading(false)
    }
}

