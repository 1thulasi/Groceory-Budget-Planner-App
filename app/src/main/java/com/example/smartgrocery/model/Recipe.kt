package com.example.smartgrocery.admin

// In Recipe.kt (under admin package)
data class Recipe(
    var recipeId: String? = null,
    var name: String? = null,
    var description: String? = null,
    var ingredients: List<Map<String, String>>? = null // <- Change this line
)
