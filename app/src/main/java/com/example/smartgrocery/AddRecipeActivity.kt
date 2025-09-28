package com.example.smartgrocery.admin

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.smartgrocery.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddRecipeActivity : AppCompatActivity() {

    private lateinit var etRecipeName: EditText
    private lateinit var etRecipeDescription: EditText
    private lateinit var btnAddIngredient: Button
    private lateinit var btnSaveRecipe: Button
    private lateinit var ingredientListLayout: LinearLayout

    private val ingredientsList = mutableListOf<Map<String, String>>()
    private lateinit var recipesRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_recipe)

        etRecipeName = findViewById(R.id.etRecipeName)
        etRecipeDescription = findViewById(R.id.etRecipeDescription)
        btnAddIngredient = findViewById(R.id.btnAddIngredient)
        btnSaveRecipe = findViewById(R.id.btnSaveRecipe)
        ingredientListLayout = findViewById(R.id.ingredientListLayout)

        recipesRef = FirebaseDatabase.getInstance().getReference("Recipes")

        btnAddIngredient.setOnClickListener {
            showAddIngredientDialog()
        }

        btnSaveRecipe.setOnClickListener {
            saveRecipe()
        }
    }

    private fun showAddIngredientDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_ingredient, null)
        val etName = dialogView.findViewById<EditText>(R.id.etIngredientName)
        val etQty = dialogView.findViewById<EditText>(R.id.etIngredientQty)
        val etUnit = dialogView.findViewById<EditText>(R.id.etIngredientUnit)

        AlertDialog.Builder(this)
            .setTitle("Add Ingredient")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val name = etName.text.toString().trim()
                val qty = etQty.text.toString().trim()
                val unit = etUnit.text.toString().trim()

                if (name.isNotEmpty() && qty.isNotEmpty() && unit.isNotEmpty()) {
                    val ingredient = mapOf(
                        "name" to name,
                        "quantity" to qty,
                        "unit" to unit
                    )
                    ingredientsList.add(ingredient)
                    showIngredientInLayout(name, qty, unit)
                    Toast.makeText(this, "Ingredient added!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showIngredientInLayout(name: String, qty: String, unit: String) {
        // Create styled ingredient TextView
        val ingredientText = TextView(this).apply {
            text = "$name - $qty $unit"
            textSize = 16f
            setTextColor(Color.BLACK)
            setPadding(0, 8, 0, 8)
        }

        // Create a divider
        val divider = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1
            ).apply {
                topMargin = 4
            }
            setBackgroundColor(Color.LTGRAY)
        }

        // Add both to the ingredient list layout
        ingredientListLayout.addView(ingredientText)
        ingredientListLayout.addView(divider)
    }

    private fun saveRecipe() {
        val name = etRecipeName.text.toString().trim()
        val description = etRecipeDescription.text.toString().trim()

        if (name.isEmpty() || description.isEmpty() || ingredientsList.isEmpty()) {
            Toast.makeText(this, "Please enter all details and at least one ingredient", Toast.LENGTH_SHORT).show()
            return
        }

        val recipeId = recipesRef.push().key!!
        val recipe = mapOf(
            "name" to name,
            "description" to description,
            "ingredients" to ingredientsList
        )

        recipesRef.child(recipeId).setValue(recipe)
            .addOnSuccessListener {
                Toast.makeText(this, "Recipe saved!", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to save recipe", Toast.LENGTH_SHORT).show()
            }
    }
}
