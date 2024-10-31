package com.maruf.devstream.data.local

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.maruf.devstream.model.Product

class Converters {

    @TypeConverter
    fun fromRating(rating: Product.Rating): String {
        return Gson().toJson(rating)
    }

    @TypeConverter
    fun toRating(ratingString: String): Product.Rating {
        val type = object : TypeToken<Product.Rating>() {}.type
        return Gson().fromJson(ratingString, type)
    }
}