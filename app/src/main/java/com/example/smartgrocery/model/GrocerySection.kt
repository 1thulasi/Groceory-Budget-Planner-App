package com.example.smartgrocery.model

sealed class GrocerySection {
    data class Header(val title: String) : GrocerySection()
    data class Item(val groceryItem: GroceryItem) : GrocerySection()
}
