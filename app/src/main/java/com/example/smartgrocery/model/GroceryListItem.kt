package com.example.smartgrocery.model

sealed class GroceryListItem {
    data class CategoryHeader(val category: String) : GroceryListItem()
    data class GroceryEntry(val item: GroceryItem) : GroceryListItem()
}
