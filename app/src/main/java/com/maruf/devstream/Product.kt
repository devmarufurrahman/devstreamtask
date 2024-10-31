package com.maruf.devstream

data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val image: String,
    val rating: Rating
) {
    data class Rating(
        val rate: Double,
        val count: Int
    )

    // Convert to Product for use in RecyclerView
    fun toProduct() = Product(id, title, price, image, rating)
}
