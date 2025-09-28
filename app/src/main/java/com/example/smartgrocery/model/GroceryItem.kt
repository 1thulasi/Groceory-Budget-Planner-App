package com.example.smartgrocery.model

import java.io.Serializable

data class GroceryItem(
    var id: String = "",
    val name: String = "",
    var price: Double = 0.0,
    var discount: Double = 0.0,
    val category: String = "",
    val unit: String = "",
    var isSelected: Boolean = false,
    var quantity: Double = 1.0
) : Serializable {
    fun getFinalPrice(): Double {
        val discountedPrice = price - (price * discount / 100)
        return discountedPrice * quantity
    }
}
