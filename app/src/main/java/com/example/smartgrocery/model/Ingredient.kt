package com.example.smartgrocery.user

import java.io.Serializable

data class Ingredient(
    var name: String = "",
    var quantity: String = "",
    var unit: String = ""
) : Serializable
