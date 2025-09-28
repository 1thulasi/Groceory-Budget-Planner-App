package com.example.smartgrocery.user

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.smartgrocery.R

class UserRecipeDetailActivity : AppCompatActivity() {

    private lateinit var txtRecipeName: TextView
    private lateinit var txtRecipeDescription: TextView
    private lateinit var ingredientsContainer: LinearLayout
    private lateinit var btnAddIngredients: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_detail)

        // Initialize views
        txtRecipeName = findViewById(R.id.txtRecipeName)
        txtRecipeDescription = findViewById(R.id.txtRecipeDescription)
        ingredientsContainer = findViewById(R.id.ingredientsContainer)
        btnAddIngredients = findViewById(R.id.btnAddToGroceryList)

        // Get recipe data passed from previous activity
        val recipe = intent.getSerializableExtra("recipe") as? RecipeModel
        if (recipe != null) {
            // Set recipe name and description
            txtRecipeName.text = recipe.recipeName
            txtRecipeDescription.text = recipe.description

            // Add ingredients to the container dynamically
            ingredientsContainer.removeAllViews()
            for (ingredient in recipe.ingredients) {
                val textView = TextView(this)
                textView.text = "${ingredient.name} - ${ingredient.quantity} ${ingredient.unit}"
                ingredientsContainer.addView(textView)
            }

            // Handle Add to Grocery List button click
            btnAddIngredients.setOnClickListener {
                // Create an intent to navigate to EditIngredientListActivity
                val intent = Intent(this, EditIngredientListActivity::class.java)
                // Pass the recipe ingredients to the next activity
                intent.putExtra("ingredients", recipe.ingredients as ArrayList<Ingredient>) // You need to serialize it
                startActivity(intent)
            }
        }
    }
}
