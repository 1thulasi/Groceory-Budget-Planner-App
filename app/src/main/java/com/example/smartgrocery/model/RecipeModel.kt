package com.example.smartgrocery.user

import java.io.Serializable

data class RecipeModel(
    var recipeId: String = "",
    var recipeName: String = "",
    var description: String = "",
    var ingredients: List<Ingredient> = listOf()
) : Serializable
