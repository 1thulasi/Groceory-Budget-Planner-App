package com.example.smartgrocery.model

sealed class ManageGroceryListItem {
    data class CategoryHeader(val category: String) : ManageGroceryListItem()
    data class GroceryData(val item: GroceryItem) : ManageGroceryListItem()
}
