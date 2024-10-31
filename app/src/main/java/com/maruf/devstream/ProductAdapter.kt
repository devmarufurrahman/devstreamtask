package com.maruf.devstream

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ProductAdapter (private val products: List<Product>) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>()  {

    inner class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImageView: ImageView = view.findViewById(R.id.productImageView)
        val productNameTextView: TextView = view.findViewById(R.id.productNameTextView)
        val productPriceTextView: TextView = view.findViewById(R.id.productPriceTextView)
        val productRatingTextView: TextView = view.findViewById(R.id.productRatingTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.productNameTextView.text = product.title
        holder.productPriceTextView.text = "$${product.price}"
        holder.productRatingTextView.text = "Rating: ${product.rating.rate}  (${product.rating.count})"


        // Load product image using Glide
        Glide.with(holder.itemView.context)
            .load(product.image)
            .into(holder.productImageView)
    }

    override fun getItemCount(): Int = products.size
}